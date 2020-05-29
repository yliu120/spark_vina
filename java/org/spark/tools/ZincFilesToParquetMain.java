package org.spark.tools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.CompoundProtos.Compound;
import org.spark_vina.SparkVinaUtils;
import scala.Tuple2;

public final class ZincFilesToParquetMain {

  private static final Logger LOGGER = LoggerFactory.getLogger(ZincFilesToParquetMain.class);
  private static final int MIN_PARTITIONS = 10;
  private static final int DEFAULT_MOL2_PARTITIONS = 100;

  public static void main(String[] args) {
    final Option sourceDirOption =
        Option.builder()
            .longOpt("source_dir")
            .required()
            .hasArg()
            .desc("The directory of the ligand in pdbqt or pdbqt.gz format.")
            .build();
    final Option outputDirOption =
        Option.builder()
            .longOpt("output_dir")
            .required()
            .hasArg()
            .desc("The output directory for writing parquet files.")
            .build();

    Options options = new Options();
    options.addOption(sourceDirOption).addOption(outputDirOption);

    // Parse the command lin arguments.
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine;
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException parseException) {
      LOGGER.error(parseException.getMessage());
      new HelpFormatter().printHelp("ZincFilesToParquet", options);
      return;
    }

    final String sourceDir = cmdLine.getOptionValue(sourceDirOption.getLongOpt());
    final String outputDir = cmdLine.getOptionValue(outputDirOption.getLongOpt());

    if (!Files.exists(Paths.get(sourceDir))) {
      LOGGER.error("Ligand Directory {} doesn't exist.", sourceDir);
      return;
    }

    Optional<List<String>> mol2LigandFilePaths = SparkVinaUtils
        .getAllLigandFilesInDirectory(sourceDir, Pattern
            .compile(".*.(mol2)"));
    Optional<List<String>> metaDataFilePaths = SparkVinaUtils
        .getAllLigandFilesInDirectory(sourceDir, Pattern
            .compile(".*.txt"));
    if (!mol2LigandFilePaths.isPresent() || mol2LigandFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting mol2 files failed.");
      return;
    }
    if (!metaDataFilePaths.isPresent() || metaDataFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting metadata files failed.");
      return;
    }

    SparkSession spark = SparkSession.builder().appName("ZincFilesToParquetMain").getOrCreate();
    JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());

    JavaRDD<String> metaDataRDD = javaSparkContext
        .textFile(String.join(",", metaDataFilePaths.get()), MIN_PARTITIONS);
    JavaRDD<Tuple2<Compound, CompoundMetadata>> compoundMetadataRDD = metaDataRDD
        .map(CompoundMetadata::parseFromTSVLine).groupBy(CompoundMetadata::getSmileString)
        .mapValues(compoundMetadataList -> compoundMetadataList.iterator().next())
        .map(tuple -> {
          Optional<Compound> metadata = ZincUtils.getMetadataFromSmileString(tuple._1);
          if (!metadata.isPresent()) {
            LOGGER.error("Discard bad molecule with malformed Smile: {}", tuple._1);
            return null;
          }
          return new Tuple2<>(metadata.get().toBuilder().setName(tuple._2.getZincId()).build(),
              tuple._2);
        }).filter(Objects::isNull);

    JavaRDD<Tuple2<Compound, String>> mol2RDD = javaSparkContext
        .parallelize(mol2LigandFilePaths.get())
        .flatMap(path -> ZincUtils.readAllMol2CompoundsFromFile(path).iterator())
        .repartition(DEFAULT_MOL2_PARTITIONS)
        .map(
            mol2String -> {
              Optional<Compound> mol2Metadata = ZincUtils.getMetadataFromMol2String(mol2String);
              if (!mol2Metadata.isPresent()) {
                LOGGER.error("Discard bad molecule with malformed mol2: {}", mol2String);
                return null;
              }
              return new Tuple2<>(mol2Metadata.get(), mol2String);
            }
        )
        .filter(Objects::isNull)
        .groupBy(tuple -> tuple._1)
        .map(
            groupByResult -> new Tuple2<>(groupByResult._1, groupByResult._2.iterator().next()._2));

    JavaRDD<Row> resultTable = JavaPairRDD.fromJavaRDD(compoundMetadataRDD)
        .join(JavaPairRDD.fromJavaRDD(mol2RDD)).map(
            compoundInfoTuple -> {
              Optional<Compound> pdbqtCompound = ZincUtils
                  .convertMol2StringToPdbqtCompound(compoundInfoTuple._2._2);
              if (!pdbqtCompound.isPresent()) {
                return null;
              }
              return RowFactory.create(
                  compoundInfoTuple._2._1.getProtomerId(),
                  compoundInfoTuple._1.getName(),
                  compoundInfoTuple._1.getNumAtoms(),
                  compoundInfoTuple._1.getNumBonds(),
                  compoundInfoTuple._1.getMolecularWeight(),
                  compoundInfoTuple._2._1.getApolarDesolvation(),
                  compoundInfoTuple._2._1.getPolarDesolvation(),
                  compoundInfoTuple._2._1.getPhLevel().name(),
                  compoundInfoTuple._2._1.getLogP(),
                  compoundInfoTuple._2._1.gethBondAcceptors(),
                  compoundInfoTuple._2._1.gethBondDonors(),
                  compoundInfoTuple._2._1.getRotatableBonds(),
                  pdbqtCompound.get()
              );
            }
        ).filter(Objects::isNull);

    spark
        .createDataFrame(resultTable, getTableSchema())
        .write()
        .parquet(outputDir);
    spark.stop();
  }

  private static StructType getTableSchema() {
    return new StructType()
        .add("protomer_id", DataTypes.StringType)
        .add("zinc_id", DataTypes.StringType)
        .add("num_atoms", DataTypes.IntegerType)
        .add("num_bonds", DataTypes.IntegerType)
        .add("molecular_weight", DataTypes.IntegerType)
        .add("apolar_desolvation", DataTypes.DoubleType)
        .add("polar_desolvation", DataTypes.DoubleType)
        .add("ph_level", DataTypes.StringType)
        .add("logp", DataTypes.DoubleType)
        .add("hbond_acceptors", DataTypes.IntegerType)
        .add("hbond_donors", DataTypes.IntegerType)
        .add("rotatable_bonds", DataTypes.IntegerType)
        .add("pdbqt", DataTypes.StringType);
  }
}
