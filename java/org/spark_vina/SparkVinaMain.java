package org.spark_vina;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SparkVinaMain {

  private static final int DEFAULT_NUM_REPEATS = 1;
  private static final int DEFAULT_NUM_MODES = 8;
  private static final int DEFAULT_NUM_TASKS = 2;
  private static final int DEFAULT_NUM_CPU_PER_TASKS = 4;
  private static final double DEFAULT_THRESHOLD = -1.0;

  private static final Logger LOGGER = LoggerFactory.getLogger(SparkVinaMain.class);

  public static void main(String[] args) throws Exception {
    final Option receptorDirOption =
        Option.builder()
            .longOpt("receptor_dir")
            .required()
            .hasArg()
            .desc("The directory containing the receptors' pdbqt files (one per receptor).")
            .build();
    final Option ligandDirOption =
        Option.builder()
            .longOpt("ligand_dir")
            .required()
            .hasArg()
            .desc("The directory of the ligand in pdbqt or pdbqt.gz format.")
            .build();
    final Option outputDirOption =
        Option.builder()
            .longOpt("output_dir")
            .required()
            .hasArg()
            .desc("The output directory")
            .build();
    final Option centerXOption =
        Option.builder()
            .longOpt("center_x")
            .hasArg()
            .type(Number.class)
            .desc("The X coord of the center of the grid.")
            .build();
    final Option centerYOption =
        Option.builder()
            .longOpt("center_y")
            .hasArg()
            .type(Number.class)
            .desc("The Y coord of the center of the grid.")
            .build();
    final Option centerZOption =
        Option.builder()
            .longOpt("center_z")
            .hasArg()
            .type(Number.class)
            .desc("The Z coord of the center of the grid.")
            .build();
    final Option sizeXOption =
        Option.builder()
            .longOpt("size_x")
            .hasArg()
            .type(Number.class)
            .desc("The X dimension of the grid.")
            .build();
    final Option sizeYOption =
        Option.builder()
            .longOpt("size_y")
            .hasArg()
            .type(Number.class)
            .desc("The Y dimension of the grid.")
            .build();
    final Option sizeZOption =
        Option.builder()
            .longOpt("size_z")
            .hasArg()
            .type(Number.class)
            .desc("The Z dimension of the grid.")
            .build();
    final Option numModesOption =
        Option.builder()
            .longOpt("num_modes")
            .hasArg()
            .type(Number.class)
            .desc("The number of calculated modes.")
            .build();
    final Option repeatsOption =
        Option.builder()
            .longOpt("repeats")
            .hasArg()
            .type(Number.class)
            .desc("Repeatedly dock a ligand for N times for calculating CI.")
            .build();
    final Option numMapTasksPerExecutorOption =
        Option.builder()
            .longOpt("num_map_tasks_per_executor")
            .hasArg()
            .type(Number.class)
            .desc("The number of spark tasks.")
            .build();
    final Option cpuPerTasksOption =
        Option.builder()
            .longOpt("cpu_per_tasks")
            .hasArg()
            .type(Number.class)
            .desc("The number of CPUs per task.")
            .build();
    final Option thresholdOption =
        Option.builder()
            .longOpt("threshold")
            .hasArg()
            .type(Number.class)
            .desc("The estimated binding free energy threshold for the docking task.")
            .build();

    Options options = new Options();
    options
        .addOption(receptorDirOption)
        .addOption(ligandDirOption)
        .addOption(outputDirOption)
        .addOption(centerXOption)
        .addOption(centerYOption)
        .addOption(centerZOption)
        .addOption(sizeXOption)
        .addOption(sizeYOption)
        .addOption(sizeZOption)
        .addOption(numModesOption)
        .addOption(repeatsOption)
        .addOption(numMapTasksPerExecutorOption)
        .addOption(cpuPerTasksOption)
        .addOption(thresholdOption);

    // Parse the command lin arguments.
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine;
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException parseException) {
      System.out.println(parseException.getMessage());
      new HelpFormatter().printHelp("SparkVinaMain", options);
      return;
    }

    // Required args.
    final String receptorDir = cmdLine.getOptionValue(receptorDirOption.getLongOpt());
    final String ligandDir = cmdLine.getOptionValue(ligandDirOption.getLongOpt());
    final String outputDir = cmdLine.getOptionValue(outputDirOption.getLongOpt());
    // Optional parameters.
    final double centerX =
        cmdLine.hasOption(centerXOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(centerXOption.getLongOpt())).doubleValue()
            : 0.0;
    final double centerY =
        cmdLine.hasOption(centerYOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(centerYOption.getLongOpt())).doubleValue()
            : 0.0;
    final double centerZ =
        cmdLine.hasOption(centerZOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(centerZOption.getLongOpt())).doubleValue()
            : 0.0;
    final double sizeX =
        cmdLine.hasOption(sizeXOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(sizeXOption.getLongOpt())).doubleValue()
            : 1.0;
    final double sizeY =
        cmdLine.hasOption(sizeYOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(sizeYOption.getLongOpt())).doubleValue()
            : 1.0;
    final double sizeZ =
        cmdLine.hasOption(centerXOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(sizeZOption.getLongOpt())).doubleValue()
            : 1.0;
    final int numRepeats =
        cmdLine.hasOption(repeatsOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(repeatsOption.getLongOpt())).intValue()
            : DEFAULT_NUM_REPEATS;
    final int numModes =
        cmdLine.hasOption(numModesOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(numModesOption.getLongOpt())).intValue()
            : DEFAULT_NUM_MODES;
    final int numMapTasksPerExecutor =
        cmdLine.hasOption(numMapTasksPerExecutorOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(numMapTasksPerExecutorOption.getLongOpt()))
            .intValue()
            : DEFAULT_NUM_TASKS;
    final int numCpuPerTasks =
        cmdLine.hasOption(cpuPerTasksOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(cpuPerTasksOption.getLongOpt())).intValue()
            : DEFAULT_NUM_CPU_PER_TASKS;
    final double threshold =
        cmdLine.hasOption(thresholdOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(thresholdOption.getLongOpt())).doubleValue()
            : DEFAULT_THRESHOLD;

    if (!Files.exists(Paths.get(receptorDir))) {
      LOGGER.error("Receptor path {} doesn't exist.", receptorDir);
      return;
    }
    if (!Files.exists(Paths.get(ligandDir))) {
      LOGGER.error("Ligand Directory {} doesn't exist.", ligandDir);
      return;
    }

    Optional<List<String>> receptorFilePaths = SparkVinaUtils
        .getAllFilesInDirectory(receptorDir, Pattern.compile(".*.pdbqt"));
    if (!receptorFilePaths.isPresent() || receptorFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting receptor files failed.");
      return;
    }

    Optional<List<String>> ligandFilePaths = SparkVinaUtils.getAllFilesInDirectory(ligandDir,
        Pattern.compile(".*.(pdbqt|pdbqt.gz)"));
    if (!ligandFilePaths.isPresent() || ligandFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting ligand files failed.");
      return;
    }

    SparkSession.Builder sparkSessionBuilder = SparkSession.builder().appName("SparkVinaMain");
    // If spark master is unset, initiate it to local.
    String masterUrl = System.getProperty("spark.master");
    boolean masterUrlNotFetchedFromJvm = (masterUrl == null || masterUrl.isEmpty());
    String masterUrlFromEnv = System.getenv("MASTER");
    boolean masterUrlNotFetchedFromEnv = (masterUrlFromEnv == null || masterUrlFromEnv.isEmpty());
    if (masterUrlNotFetchedFromJvm && masterUrlNotFetchedFromEnv) {
      sparkSessionBuilder.master("local[*]");
    }
    SparkSession spark = sparkSessionBuilder.getOrCreate();
    JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());

    final int numOfExecutors =
        Integer.parseInt(javaSparkContext.getConf().get("spark.executor.instances", "1"));
    LOGGER.info("SparkVina application runs with {} executors.", numOfExecutors);

    // Set up accumulators
    LongAccumulator numModelsProduced = javaSparkContext.sc().longAccumulator("NumModelsProduced");
    LongAccumulator numModelsProcessed =
        javaSparkContext.sc().longAccumulator("NumModelsProcessed");
    LongAccumulator numModelsFit = javaSparkContext.sc().longAccumulator("NumModelsFit");

    // Loads ligands
    JavaRDD<String> ligands =
        javaSparkContext
            .parallelize(ligandFilePaths.get())
            .map(VinaTools::readLigandsToStrings)
            .setName("ReadLigandsToStrings")
            .flatMap(
                ligandStrings ->
                    ligandStrings.stream()
                        .flatMap(
                            ligandString -> Collections.nCopies(numRepeats, ligandString).stream())
                        .collect(Collectors.toList())
                        .iterator())
            .setName("GenerateRepeatedLigandsRDD");

    JavaRDD<Row> finalResult = javaSparkContext.emptyRDD();
    for (String receptorPath : receptorFilePaths.get()) {
      JavaRDD<Row> result = ligands.repartition(numMapTasksPerExecutor * numOfExecutors)
          .map(
              model -> {
                numModelsProduced.add(1);
                return model;
              })
          .map(
              new FitCompoundFunction(
                  receptorPath,
                  centerX,
                  centerY,
                  centerZ,
                  sizeX,
                  sizeY,
                  sizeZ,
                  numCpuPerTasks,
                  numModes,
                  threshold))
          .setName("DockingCompoundsToReceptor")
          .map(
              vinaResult -> {
                numModelsProcessed.add(1);
                return vinaResult;
              })
          .filter(
              vinaResult -> {
                boolean notNull = (vinaResult != null);
                if (notNull) {
                  numModelsFit.add(1);
                }
                return notNull;
              })
          .keyBy(vinaResult -> vinaResult.getLigandId())
          .setName("KeyVinaResultByLigandId")
          .groupByKey()
          .setName("GroupVinaResultByLigandId")
          .map(pair -> new DockingResult(pair._1, pair._2))
          .setName("MapToDockingResult")
          .map(
              dockingResult ->
                  // See getDockingResultSchema() for the definition of the schema.
                  RowFactory.create(
                      dockingResult.getCompoundKey(),
                      receptorPath,
                      dockingResult.getOriginalPdbqt(),
                      dockingResult.getNumModels(),
                      dockingResult.getAffinityMean(),
                      dockingResult.getAffinityStd(),
                      dockingResult.getVinaResults()))
          .setName("CreateParquetRowFromDockingResult");
      finalResult = finalResult.union(result).setName("UnionResult");
    }

    LOGGER.info("Result RDD execution plan: \n {}", finalResult.toDebugString());

    spark
        .createDataFrame(finalResult, getDockingResultSchema())
        .write()
        .parquet(outputDir);

    LOGGER.info("Accumulator numModelsProduced: {}", numModelsProduced.value());
    LOGGER.info("Accumulator numModelsProcessed: {}", numModelsProcessed.value());
    LOGGER.info("Accumulator numModelsFit: {}", numModelsFit.value());
    spark.stop();
  }

  private static StructType getDockingResultSchema() {
    StructType vinaResultSchema =
        new StructType(
            new StructField[]{
                new StructField("affinity", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("docked_pdbqt", DataTypes.StringType, false, Metadata.empty()),
            });
    StructType vinaResultsSchema =
        new StructType(
            new StructField[]{
                new StructField("random_seed", DataTypes.LongType, false, Metadata.empty()),
                new StructField(
                    "vina_result",
                    DataTypes.createArrayType(vinaResultSchema, false),
                    false,
                    Metadata.empty()),
            });
    return new StructType(
        new StructField[]{
            new StructField("name", DataTypes.StringType, false, Metadata.empty()),
            new StructField("receptor_path", DataTypes.StringType, false, Metadata.empty()),
            new StructField("original_pdbqt", DataTypes.StringType, false, Metadata.empty()),
            new StructField("num_models", DataTypes.IntegerType, false, Metadata.empty()),
            new StructField("affinity_mean", DataTypes.DoubleType, false, Metadata.empty()),
            new StructField("affinity_std", DataTypes.DoubleType, false, Metadata.empty()),
            new StructField(
                "vina_results",
                DataTypes.createArrayType(vinaResultsSchema, false),
                false,
                Metadata.empty()),
        });
  }
}
