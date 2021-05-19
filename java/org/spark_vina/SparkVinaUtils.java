package org.spark_vina;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkVinaUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SparkVinaUtils.class);

  public static Optional<List<String>> getAllFilesInDirectory(String directory,
      Pattern fileNamePattern) {
    try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
      List<String> result =
          paths
              .filter(
                  path -> fileNamePattern.matcher(path.toString()).matches())
              .map(Path::toAbsolutePath)
              .map(Path::toString)
              .collect(Collectors.toList());
      LOGGER.info("Read {} files from {} in total.", result.size(), directory);
      return Optional.of(result);
    } catch (IOException e) {
      LOGGER.info("Failed to walk the input directory: {}.", directory);
      e.printStackTrace();
    }
    return Optional.empty();
  }
}
