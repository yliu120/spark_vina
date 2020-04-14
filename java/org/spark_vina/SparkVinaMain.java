package org.spark_vina;

import java.util.ArrayList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

public final class SparkVinaMain {

  public static void main(String[] args) throws Exception {
    Options options = new Options();

    final Option sparkMaster = Option.builder().longOpt("spark_master")
        .desc("Spark Master Address").build();
    options.addOption(sparkMaster);
    final Option receptorPath = Option.builder().longOpt("receptor_path").required()
        .desc("The path of the receptor's pdbqt file.").build();
    options.addOption(receptorPath);
    final Option ligandDir = Option.builder().longOpt("ligand_dir").required()
        .desc("The directory of the ligand in pdbqt or pdbqt.gz format.").build();
    options.addOption(ligandDir);
    final Option outputDir = Option.builder().longOpt("output_dir").required()
        .desc("The output directory")
        .build();
    options.addOption(outputDir);
    final Option centerX = Option.builder().longOpt("center_x").type(float.class)
        .desc("The X coord of the center of the grid.").build();
    options.addOption(centerX);
    final Option centerY = Option.builder().longOpt("center_y").type(float.class)
        .desc("The Y coord of the center of the grid.").build();
    options.addOption(centerY);
    final Option centerZ = Option.builder().longOpt("center_z").type(float.class)
        .desc("The Z coord of the center of the grid.").build();
    options.addOption(centerZ);
    final Option sizeX = Option.builder().longOpt("size_x").type(float.class)
        .desc("The X dimension of the grid.").build();
    options.addOption(sizeX);
    final Option sizeY = Option.builder().longOpt("size_y").type(float.class)
        .desc("The Y dimension of the grid.").build();
    options.addOption(sizeY);
    final Option sizeZ = Option.builder().longOpt("size_z").type(float.class)
        .desc("The Z dimension of the grid.").build();
    options.addOption(
        sizeZ);
    final Option numModes = Option.builder().longOpt("num_modes").type(int.class)
        .desc("The number of calculated modes.").build();
    options.addOption(numModes);
    final Option numTasks = Option.builder().longOpt("num_tasks").type(int.class)
        .desc("The number of spark tasks.").build();
    options.addOption(
        numTasks);
    final Option cpuPerTasks = Option.builder().longOpt("cpu_per_tasks").type(int.class)
        .desc("The number of CPUs per task.").build();
    options.addOption(cpuPerTasks);
    final Option threshold = Option.builder().longOpt("threshold").type(float.class)
        .desc("The estimated binding free energy threshold for the docking task.").build();
    options.addOption(threshold);

    try {
      // Parse the command lin arguments.
      CommandLineParser parser = new DefaultParser();
      CommandLine cmdLine = parser.parse(options, args);

      String sparkMasterValue;
      if (cmdLine.hasOption(sparkMaster.getLongOpt())) {
        sparkMasterValue = cmdLine.getOptionValue(sparkMaster.getLongOpt());
      }

    } catch (ParseException parseException) {
      System.out.println(parseException.getMessage());
      new HelpFormatter().printHelp("SparkVinaMain", options);
      return;
    }

    // SparkSession spark = SparkSession
    //     .builder()
    //     .appName("SparkVinaMain")
    //     .getOrCreate();
    // spark.stop();
  }
}