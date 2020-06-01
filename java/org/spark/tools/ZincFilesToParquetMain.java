package org.spark.tools;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import org.apache.spark.SparkConf;
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
  private static final int DEFAULT_PARTITIONS = 10;

  public static void main(String[] args) {
    final Option sourceDirOption =
        Option.builder()
            .longOpt("source_dir")
            .required()
            .hasArg()
            .desc("The directory of the ligand in pdbqt or pdbqt.gz format.")
            .build();
    final Option numShardsOption =
        Option.builder()
            .longOpt("num_shards")
            .hasArg()
            .desc("The number of output shards.")
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
    final int numShards = cmdLine.hasOption(numShardsOption.getLongOpt()) ?
        Integer.parseInt(cmdLine.getOptionValue(numShardsOption.getLongOpt())) : DEFAULT_PARTITIONS;

    if (!Files.exists(Paths.get(sourceDir))) {
      LOGGER.error("Ligand Directory {} doesn't exist.", sourceDir);
      return;
    }

    Optional<List<String>> mol2LigandFilePaths = SparkVinaUtils
        .getAllLigandFilesInDirectory(sourceDir, Pattern
            .compile(".*.mol2"));
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

    // Unfortunately, babel library is not thread-safe. So we enforce each executor running one
    // task at a time.
    SparkConf conf = new SparkConf().set("spark.executor.cores", "1");
    SparkSession spark = SparkSession
        .builder()
        .appName("ZincFilesToParquetMain")
        .config(conf)
        .getOrCreate();
    JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());

    JavaPairRDD<Compound, CompoundMetadata> metaDataPairRDD = javaSparkContext
        .textFile(String.join(",", metaDataFilePaths.get()), MIN_PARTITIONS)
        .mapToPair(line -> {
          CompoundMetadata metadata = CompoundMetadata.parseFromTSVLine(line);
          return new Tuple2<>(metadata.getProtomerId(), metadata);
        })
        .reduceByKey((a, b) -> a)
        .values()
        .mapToPair(metadata -> {
          Optional<Compound> compound = ZincUtils
              .getMetadataFromSmileString(metadata.getSmileString());
          if (!compound.isPresent()) {
            LOGGER.error(
                "Discard bad molecule with malformed Smile: {}", metadata.getSmileString());
            return null;
          }
          return new Tuple2<>(compound.get().toBuilder().setName(metadata.getZincId()).build(),
              metadata);
        })
        .filter(Objects::nonNull);
    long smileCount = metaDataPairRDD.count();
    LOGGER.info("smile count: {}", smileCount);

    JavaPairRDD<Compound, String> mol2PairRDD = javaSparkContext
        .parallelize(mol2LigandFilePaths.get())
        .flatMap(path -> ZincUtils.readAllMol2CompoundsFromFile(path).iterator())
        .repartition(numShards)
        .mapToPair(mol2String -> new Tuple2<>(mol2String.substring(0, 200), mol2String))
        .reduceByKey((a, b) -> a)
        .values()
        .mapToPair(
            mol2String -> {
              Optional<Compound> mol2Metadata = ZincUtils.getMetadataFromMol2String(mol2String);
              if (!mol2Metadata.isPresent()) {
                LOGGER.error("Discard bad molecule with malformed mol2: {}", mol2String);
                return null;
              }
              return new Tuple2<>(mol2Metadata.get(), mol2String);
            }
        )
        .filter(Objects::nonNull);
    long mol2Count = mol2PairRDD.count();
    LOGGER.info("mol2 count: {}", mol2Count);

    JavaRDD<Row> resultTableRDD = metaDataPairRDD.cogroup(mol2PairRDD).map(
        coGroupResult -> {
          final Compound key = coGroupResult._1;
          List<String> protomerIds = new ArrayList<>();
          List<String> phLevels = new ArrayList<>();
          double averageLogP = 0.0;
          int numberOfMetadata = 0;
          int numberRotatableBonds = 0;
          double apolarDesolvation = 0.0;
          double polarDesolvation = 0.0;
          int hBondDonors = 0;
          int hBondAcceptors = 0;
          for (CompoundMetadata metadata : coGroupResult._2._1) {
            protomerIds.add(metadata.getProtomerId());
            phLevels.add(metadata.getPhLevel().name());
            averageLogP += metadata.getLogP();
            if (numberRotatableBonds == 0) {
              numberRotatableBonds = metadata.getRotatableBonds();
            }
            if (apolarDesolvation == 0.0) {
              apolarDesolvation = metadata.getApolarDesolvation();
            }
            if (polarDesolvation == 0.0) {
              polarDesolvation = metadata.getPolarDesolvation();
            }
            if (hBondAcceptors == 0) {
              hBondAcceptors = metadata.gethBondAcceptors();
            }
            if (hBondDonors == 0) {
              hBondDonors = metadata.gethBondDonors();
            }
            ++numberOfMetadata;
          }
          averageLogP = averageLogP / numberOfMetadata;

          List<String> pdbqtStrings = new ArrayList<>();
          for (String mol2String : coGroupResult._2._2) {
            String pdbqtString = ZincUtils.convertMol2ToPdbqt(mol2String);
            if (!pdbqtString.isEmpty()) {
              pdbqtStrings.add(pdbqtString);
            }
          }

          return RowFactory.create(
              key.getName(),
              key.getNumAtoms(),
              key.getNumBonds(),
              (double) key.getMolecularWeightMillis() / 1000.0,
              key.getNetCharge(),
              protomerIds.toArray(new String[protomerIds.size()]),
              phLevels.toArray(new String[phLevels.size()]),
              averageLogP,
              numberRotatableBonds,
              apolarDesolvation,
              polarDesolvation,
              hBondDonors,
              hBondAcceptors,
              pdbqtStrings.toArray(new String[pdbqtStrings.size()])
          );
        }
    );

    long rowCount = resultTableRDD.count();
    LOGGER.info("result table row count: {}", rowCount);

    spark
        .createDataFrame(resultTableRDD, getTableSchema())
        .write()
        .parquet(outputDir);
    spark.stop();
  }

  private static StructType getTableSchema() {
    return new StructType()
        .add("zinc_id", DataTypes.StringType)
        .add("num_atoms", DataTypes.IntegerType)
        .add("num_bonds", DataTypes.IntegerType)
        .add("molecular_weight", DataTypes.DoubleType)
        .add("net_charge", DataTypes.IntegerType)
        .add("protomer_ids", DataTypes.createArrayType(DataTypes.StringType, false))
        .add("ph_levels", DataTypes.createArrayType(DataTypes.StringType, false))
        .add("logp", DataTypes.DoubleType)
        .add("rotatable_bonds", DataTypes.IntegerType)
        .add("apolar_desolvation", DataTypes.DoubleType)
        .add("polar_desolvation", DataTypes.DoubleType)
        .add("hbond_acceptors", DataTypes.IntegerType)
        .add("hbond_donors", DataTypes.IntegerType)
        .add("pdbqts", DataTypes.createArrayType(DataTypes.StringType, false));
  }
}
