package org.spark.tools;

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
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.SparkVinaUtils;

public final class ZincFilesToParquetMain {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZincFilesToParquetMain.class);

  public static void main(String[] args) {
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

    Options options = new Options();
    options.addOption(ligandDirOption).addOption(outputDirOption);

    // Parse the command lin arguments.
    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine = null;
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException parseException) {
      LOGGER.error(parseException.getMessage());
      new HelpFormatter().printHelp("SparkVinaMain", options);
      return;
    }

    final String ligandDir = cmdLine.getOptionValue(ligandDirOption.getLongOpt());
    final String outputDir = cmdLine.getOptionValue(outputDirOption.getLongOpt());

    if (!Files.exists(Paths.get(ligandDir))) {
      LOGGER.error("Ligand Directory {} doesn't exist.", ligandDir);
      return;
    }

    Optional<List<String>> ligandFilePaths = SparkVinaUtils.getAllLigandFilesInDirectory(ligandDir);
    if (!ligandFilePaths.isPresent() || ligandFilePaths.get().isEmpty()) {
      LOGGER.error("Collecting ligand pdbqt files failed.");
      return;
    }

    SparkSession spark = SparkSession.builder().appName("ZincFilesToParquetMain").getOrCreate();
    JavaSparkContext javaSparkContext = new JavaSparkContext(spark.sparkContext());

    JavaRDD<Row> rows = javaSparkContext.parallelize(ligandFilePaths.get()).map(path -> )
  }
}
