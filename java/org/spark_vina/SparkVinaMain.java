package org.spark_vina;

import com.google.common.base.Optional;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.SparkVinaProtos.VinaResult;

public final class SparkVinaMain {
  private static final int kDefaultNumModes = 8;
  private static final int kDefaultNumTasks = 1;
  private static final int kDefaultNumCpuPerTasks = 4;
  private static final double kDefaultThreshold = -1.0;
  private static final int kDefaultOutputNumber = 1000;

  private static final Logger LOGGER = LoggerFactory.getLogger(SparkVinaMain.class);

  public static void main(String[] args) throws Exception {
    final Option sparkMasterOption =
        Option.builder().longOpt("spark_master").desc("Spark Master Address").build();
    final Option receptorPathOption =
        Option.builder()
            .longOpt("receptor_path")
            .required()
            .hasArg()
            .desc("The path of the receptor's pdbqt file.")
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
    final Option numTasksOption =
        Option.builder()
            .longOpt("num_tasks")
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
    final Option numberOutputOption =
        Option.builder()
            .longOpt("num_output")
            .hasArg()
            .type(Number.class)
            .desc("Output the top N ligands with the best binding score.")
            .build();

    Options options = new Options();
    options
        .addOption(sparkMasterOption)
        .addOption(receptorPathOption)
        .addOption(ligandDirOption)
        .addOption(outputDirOption)
        .addOption(centerXOption)
        .addOption(centerYOption)
        .addOption(centerZOption)
        .addOption(sizeXOption)
        .addOption(sizeYOption)
        .addOption(sizeZOption)
        .addOption(numModesOption)
        .addOption(numTasksOption)
        .addOption(cpuPerTasksOption)
        .addOption(thresholdOption)
        .addOption(numberOutputOption);

    // Parse the command lin arguments.
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine = null;
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException parseException) {
      System.out.println(parseException.getMessage());
      new HelpFormatter().printHelp("SparkVinaMain", options);
    }

    // Default to local spark cluster.
    final String sparkMaster = cmdLine.getOptionValue(sparkMasterOption.getLongOpt(), "local[*]");
    // Required args.
    final String receptorPath = cmdLine.getOptionValue(receptorPathOption.getLongOpt());
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
    final int numModes =
        cmdLine.hasOption(numModesOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(numModesOption.getLongOpt())).intValue()
            : kDefaultNumModes;
    final int numTasks =
        cmdLine.hasOption(numTasksOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(numTasksOption.getLongOpt())).intValue()
            : kDefaultNumTasks;
    final int numCpuPerTasks =
        cmdLine.hasOption(cpuPerTasksOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(cpuPerTasksOption.getLongOpt())).intValue()
            : kDefaultNumCpuPerTasks;
    final double threshold =
        cmdLine.hasOption(thresholdOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(thresholdOption.getLongOpt())).doubleValue()
            : kDefaultThreshold;
    final int numOutput =
        cmdLine.hasOption(numberOutputOption.getLongOpt())
            ? ((Number) cmdLine.getParsedOptionValue(numberOutputOption.getLongOpt())).intValue()
            : kDefaultOutputNumber;

    if (!Files.exists(Paths.get(receptorPath))) {
      LOGGER.error("Receptor path {} doesn't exist.", receptorPath);
      return;
    }
    if (!Files.exists(Paths.get(ligandDir))) {
      LOGGER.error("Ligand Directory {} doesn't exist.", ligandDir);
      return;
    }

    Optional<List<String>> ligandFilePaths = SparkVinaUtils.getAllLigandFilesInDirectory(ligandDir);
    if (!ligandFilePaths.isPresent() || ligandFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting ligand files failed.");
      return;
    }

    SparkContext sparkContext =
        new SparkContext(new SparkConf().setMaster(sparkMaster).setAppName("SparkVina"));
    SparkSession spark =
        SparkSession.builder().appName("SparkVinaMain").sparkContext(sparkContext).getOrCreate();
    JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());

    List<VinaResult> result =
        javaSparkContext
            .parallelize(ligandFilePaths.get())
            .map(VinaTools::readLigandsToStrings)
            .flatMap(List::iterator)
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
            .filter(Optional::isPresent)
            .map(Optional::get)
            .takeOrdered(numOutput, new VinaResultComparator());

    spark
        .createDataFrame(result, VinaResult.class)
        .write()
        .parquet(Paths.get(outputDir, "output.parquet").toAbsolutePath().toString());
    spark.stop();
  }
}
