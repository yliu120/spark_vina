package org.spark_vina;

import com.google.common.base.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
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

public final class SparkVinaMain {
  private static final int kDefaultNumModes = 8;
  private static final int kDefaultNumTasks = 1;
  private static final int kDefaultNumCpuPerTasks = 4;
  private static final double kDefaultThreshold = 1.0;

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
            .type(double.class)
            .desc("The X coord of the center of the grid.")
            .build();
    final Option centerYOption =
        Option.builder()
            .longOpt("center_y")
            .hasArg()
            .type(double.class)
            .desc("The Y coord of the center of the grid.")
            .build();
    final Option centerZOption =
        Option.builder()
            .longOpt("center_z")
            .hasArg()
            .type(double.class)
            .desc("The Z coord of the center of the grid.")
            .build();
    final Option sizeXOption =
        Option.builder()
            .longOpt("size_x")
            .hasArg()
            .type(double.class)
            .desc("The X dimension of the grid.")
            .build();
    final Option sizeYOption =
        Option.builder()
            .longOpt("size_y")
            .hasArg()
            .type(double.class)
            .desc("The Y dimension of the grid.")
            .build();
    final Option sizeZOption =
        Option.builder()
            .longOpt("size_z")
            .hasArg()
            .type(double.class)
            .desc("The Z dimension of the grid.")
            .build();
    final Option numModesOption =
        Option.builder()
            .longOpt("num_modes")
            .hasArg()
            .type(int.class)
            .desc("The number of calculated modes.")
            .build();
    final Option numTasksOption =
        Option.builder()
            .longOpt("num_tasks")
            .hasArg()
            .type(int.class)
            .desc("The number of spark tasks.")
            .build();
    final Option cpuPerTasksOption =
        Option.builder()
            .longOpt("cpu_per_tasks")
            .hasArg()
            .type(int.class)
            .desc("The number of CPUs per task.")
            .build();
    final Option thresholdOption =
        Option.builder()
            .longOpt("threshold")
            .hasArg()
            .type(double.class)
            .desc("The estimated binding free energy threshold for the docking task.")
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
        .addOption(thresholdOption);

    try {
      // Parse the command lin arguments.
      CommandLineParser parser = new DefaultParser();
      CommandLine cmdLine = parser.parse(options, args);

      // Default to local spark cluster.
      final String sparkMaster = cmdLine.getOptionValue(sparkMasterOption.getLongOpt(), "local[*]");
      // Required args.
      final String receptorPath = cmdLine.getOptionValue(receptorPathOption.getLongOpt());
      final String ligandDir = cmdLine.getOptionValue(ligandDirOption.getLongOpt());
      final String outputDir = cmdLine.getOptionValue(outputDirOption.getLongOpt());
      // Optional parameters.
      final double centerX =
          cmdLine.hasOption(centerXOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(centerXOption.getLongOpt())
              : 0.0;
      final double centerY =
          cmdLine.hasOption(centerYOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(centerYOption.getLongOpt())
              : 0.0;
      final double centerZ =
          cmdLine.hasOption(centerZOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(centerZOption.getLongOpt())
              : 0.0;
      final double sizeX =
          cmdLine.hasOption(sizeXOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(sizeXOption.getLongOpt())
              : 0.0;
      final double sizeY =
          cmdLine.hasOption(sizeYOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(sizeYOption.getLongOpt())
              : 0.0;
      final double sizeZ =
          cmdLine.hasOption(centerXOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(sizeZOption.getLongOpt())
              : 0.0;
      final int numModes =
          cmdLine.hasOption(numModesOption.getLongOpt())
              ? (int) cmdLine.getParsedOptionValue(numModesOption.getLongOpt())
              : kDefaultNumModes;
      final int numTasks =
          cmdLine.hasOption(numTasksOption.getLongOpt())
              ? (int) cmdLine.getParsedOptionValue(numTasksOption.getLongOpt())
              : kDefaultNumTasks;
      final int numCpuPerTasks =
          cmdLine.hasOption(cpuPerTasksOption.getLongOpt())
              ? (int) cmdLine.getParsedOptionValue(cpuPerTasksOption.getLongOpt())
              : kDefaultNumCpuPerTasks;
      final double threshold =
          cmdLine.hasOption(thresholdOption.getLongOpt())
              ? (double) cmdLine.getParsedOptionValue(thresholdOption.getLongOpt())
              : kDefaultThreshold;

      if (!Files.exists(Paths.get(receptorPath))) {
        throw new ParseException("Receptor path doesn't exist.");
      }
      if (!Files.exists(Paths.get(ligandDir))) {
        throw new ParseException("Ligand directory doesn't exist.");
      }

      SparkContext sparkContext =
          new SparkContext(new SparkConf().setMaster(sparkMaster).setAppName("SparkVina"));
      SparkSession spark =
          SparkSession.builder().appName("SparkVinaMain").sparkContext(sparkContext).getOrCreate();
      JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());

      List<String> result =
          javaSparkContext
              .parallelize(SparkVinaUtils.getAllLigandFilesInDirectory(ligandDir).get())
              .map(VinaTools::readLigandsToStrings)
              .flatMap(List::iterator)
              .collect();

      spark.stop();
    } catch (ParseException parseException) {
      System.out.println(parseException.getMessage());
      new HelpFormatter().printHelp("SparkVinaMain", options);
    }
  }
}
