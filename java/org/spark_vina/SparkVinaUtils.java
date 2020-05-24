package org.spark_vina;

import com.google.common.base.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkVinaUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SparkVinaUtils.class);

  public static Optional<List<String>> getAllLigandFilesInDirectory(String ligandDir,
      Pattern fileNamePattern) {
    try (Stream<Path> paths = Files.walk(Paths.get(ligandDir))) {
      List<String> result =
          paths
              .filter(
                  path -> fileNamePattern.matcher(path.toString()).matches())
              .map(Path::toAbsolutePath)
              .map(Path::toString)
              .collect(Collectors.toList());
      LOGGER.info("Read {} files in total.", result.size());
      return Optional.of(result);
    } catch (IOException e) {
      LOGGER.info("Failed to walk the input ligand directory: {}.", ligandDir);
      e.printStackTrace();
    }
    return Optional.absent();
  }
}
