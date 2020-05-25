package org.spark.tools;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.CompoundProtos.AtomFeatures;
import org.spark_vina.CompoundProtos.AtomType;
import org.spark_vina.CompoundProtos.Compound;
import org.spark_vina.SparkVinaUtils;
import org.spark_vina.VinaTools;
import org.spark_vina.util.PdbqtParserHelper;
import org.spark_vina.util.ZincHelper;

public final class ZincFilesToParquetMain {

  private static final Logger LOGGER = LoggerFactory.getLogger(ZincFilesToParquetMain.class);
  private static final int DEFAULT_NUM_SHARDS = 1;

  public static void main(String[] args) throws Exception {
    final Option sourceDirOption =
        Option.builder()
            .longOpt("source_dir")
            .required()
            .hasArg()
            .desc("The directory of the ligand in pdbqt or pdbqt.gz format.")
            .build();
    final Option shardsOption =
        Option.builder()
            .longOpt("shards")
            .required()
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
    options.addOption(sourceDirOption).addOption(shardsOption).addOption(outputDirOption);

    // Parse the command lin arguments.
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine = null;
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException parseException) {
      LOGGER.error(parseException.getMessage());
      new HelpFormatter().printHelp("ZincFilesToParquet", options);
      return;
    }

    final String sourceDir = cmdLine.getOptionValue(sourceDirOption.getLongOpt());
    final int shards =
        cmdLine.hasOption(shardsOption.getLongOpt())
            ? Integer.parseInt(cmdLine.getOptionValue(shardsOption.getLongOpt()))
            : DEFAULT_NUM_SHARDS;
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

    for (String path : mol2LigandFilePaths.get()) {
      for (String mol2Ligand : ZincUtils.readAllMol2CompoundsFromFile(path)) {
        Optional<Compound> compound = ZincUtils.convertMol2StringToPdbqtCompound(mol2Ligand);
        if (compound.isPresent()) {
          LOGGER.info("Converted compound:\n{}", compound.get().toString());
        }
      }
    }

//    SparkSession spark = SparkSession.builder().appName("ZincFilesToParquetMain").getOrCreate();
//    JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());
//
////    LongAccumulator numCompounds = javaSparkContext.sc().longAccumulator("NumCompounds");
//
////    JavaRDD<Row> resultRows = javaSparkContext.parallelize(ligandFilePaths.get()).map(path -> {
////      List<String> ligandStrings = VinaTools.readLigandsToStrings(path);
////      List<Compound> compounds = new ArrayList<>(ligandStrings.size());
////      for (final String ligandString : ligandStrings) {
////        Optional<Compound> compound = PdbqtParserHelper.parseFeaturesFromPdbqtString(ligandString);
////        if (!compound.isPresent()) {
////          LOGGER.error("Ligand cannot be converted to a compound: {}", ligandString);
////          continue;
////        }
////        compounds.add(compound.get());
////      }
////      numCompounds.add(compounds.size());
////      return compounds;
////    }).flatMap(compounds -> compounds.iterator()).repartition(shards).distinct().map(compound -> {
////      HashMap<AtomType, Integer> countMap = new HashMap<>();
////      for (AtomFeatures features : compound.getAtomFeaturesList()) {
////        countMap.put(features.getAtomType(), features.getCount());
////      }
////      return RowFactory
////          .create(compound.getName(), compound.getMolecularWeight(), compound.getLogP(),
////              countMap.getOrDefault(AtomType.CARBON, 0),
////              countMap.getOrDefault(AtomType.NITROGEN, 0),
////              countMap.getOrDefault(AtomType.OXYGEN, 0),
////              countMap.getOrDefault(AtomType.PHOSPHORUS, 0),
////              countMap.getOrDefault(AtomType.SULFUR, 0), compound.getOriginalPdbqt());
////    });
//
//    spark
//        .createDataFrame(resultRows, getTableSchema())
//        .write()
//        .parquet(outputDir);
//    spark.stop();
  }

  private static StructType getTableSchema() {
    return new StructType().add("name", DataTypes.StringType)
        .add("molecular_weight", DataTypes.IntegerType)
        .add("logp", DataTypes.DoubleType)
        .add("num_carbons", DataTypes.IntegerType)
        .add("num_nitrogens", DataTypes.IntegerType)
        .add("num_oxygens", DataTypes.IntegerType)
        .add("num_phosphorus", DataTypes.IntegerType)
        .add("num_sulfers", DataTypes.IntegerType)
        .add("pdbqt", DataTypes.StringType);
  }
}
