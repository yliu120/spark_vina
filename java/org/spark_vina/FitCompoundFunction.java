package org.spark_vina;

import com.google.common.base.Optional;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.SparkVinaProtos.VinaResult;

public class FitCompoundFunction implements Function<String, Optional<VinaResult>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FitCompoundFunction.class);
  private static final String ZINC_PATTERN = "ZINC";

  FitCompoundFunction(
      String receptorPath,
      double centerX,
      double centerY,
      double centerZ,
      double sizeX,
      double sizeY,
      double sizeZ,
      int cpu,
      int numModes,
      double filterLimit) {
    this.receptorPath = receptorPath;
    this.centerX = centerX;
    this.centerY = centerY;
    this.centerZ = centerZ;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.sizeZ = sizeZ;
    this.cpu = cpu;
    this.numModes = numModes;
    this.filterLimit = filterLimit;
  }

  @Override
  public Optional<VinaResult> call(String ligandString) throws Exception {
    Optional<VinaResult> vinaResult = vinaDock.vinaFitSingleLigand(ligandString, filterLimit);
    if (!vinaResult.isPresent()) {
      LOGGER.warn("Cannot fit ligand: {}", ligandString);
    }
    Optional<String> ligandKey = parseLigandKey(vinaResult.get().getLigandStr());
    if (!ligandKey.isPresent()) {
      LOGGER.error("Ligand with no ZINC id: {}", vinaResult.get().getLigandStr());
    }
    return Optional.of(vinaResult.get().toBuilder().setLigandId(ligandKey.get()).build());
  }

  private Optional<String> parseLigandKey(String ligandString) {
    String[] lines = ligandString.split("\\r?\\n");
    java.util.Optional<String> keyLine =
        Arrays.stream(lines).filter(line -> line.contains(ZINC_PATTERN)).findFirst();
    if (!keyLine.isPresent()) {
      return Optional.absent();
    }
    String[] components = keyLine.get().split(" ");
    java.util.Optional<String> ligandId =
        Arrays.stream(components).filter(word -> word.contains(ZINC_PATTERN)).findFirst();
    return ligandId.isPresent() ? Optional.of(ligandId.get()) : Optional.absent();
  }

  private void readObject(ObjectInputStream inputStream)
      throws IOException, ClassNotFoundException {
    inputStream.defaultReadObject();
    vinaDock =
        new VinaDock(receptorPath, centerX, centerY, centerZ, sizeX, sizeY, sizeZ, cpu, numModes);
    LOGGER.info("FitCompoundFunction got deserialized.");
  }

  private void writeObject(ObjectOutputStream outputStream) throws IOException {
    outputStream.defaultWriteObject();
  }

  private transient VinaDock vinaDock;
  private final String receptorPath;
  private final double centerX;
  private final double centerY;
  private final double centerZ;
  private final double sizeX;
  private final double sizeY;
  private final double sizeZ;
  private final int cpu;
  private final int numModes;
  private final double filterLimit;

  private static final long serialVersionUID = 333;
}
