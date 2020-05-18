package org.spark_vina;

import com.google.common.base.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkVinaUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(SparkVinaUtils.class);

  public static Optional<List<String>> getAllLigandFilesInDirectory(String ligandDir) {
    try (Stream<Path> paths = Files.walk(Paths.get(ligandDir))) {
      List<String> result =
          paths
              .filter(
                  path -> path.toString().endsWith("pdbqt") || path.toString().endsWith("pdbqt.gz"))
              .map(Path::toAbsolutePath)
              .map(Path::toString)
              .collect(Collectors.toList());
      LOGGER.info("Read {} ligand files in total.", result.size());
      return Optional.of(result);
    } catch (IOException e) {
      LOGGER.info("Failed to walk the input ligand directory: {}.", ligandDir);
      e.printStackTrace();
    }
    return Optional.absent();
  }
}
