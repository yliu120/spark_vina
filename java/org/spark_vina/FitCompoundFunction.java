package org.spark_vina;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Optional;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.SparkVinaProtos.VinaResult;

public class FitCompoundFunction implements Function<String, VinaResult> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FitCompoundFunction.class);
  private static final String ZINC_PATTERN = "ZINC";
  private static final long serialVersionUID = 333;
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
  private transient VinaDock vinaDock;

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
  public VinaResult call(String ligandString) throws Exception {
    Optional<VinaResult> vinaResult = vinaDock.vinaFitSingleLigand(ligandString, filterLimit);
    if (!vinaResult.isPresent()) {
      LOGGER.warn("Cannot fit ligand: {}", ligandString);
      return null;
    }
    String ligandKey = parseLigandKey(vinaResult.get().getOriginalPdbqt());
    if (ligandKey == null) {
      LOGGER.error("Ligand with no ZINC id: {}", vinaResult.get().getOriginalPdbqt());
      return null;
    }
    return vinaResult.get().toBuilder().setLigandId(ligandKey).build();
  }

  private String parseLigandKey(String ligandString) {
    String[] lines = ligandString.split("\\r?\\n");
    java.util.Optional<String> keyLine =
        Arrays.stream(lines).filter(line -> line.contains(ZINC_PATTERN)).findFirst();
    if (!keyLine.isPresent()) {
      return null;
    }
    String[] components = keyLine.get().split(" ");
    Optional<String> ligandId =
        Arrays.stream(components).filter(word -> word.contains(ZINC_PATTERN)).findFirst();
    return ligandId.isPresent() ? ligandId.get() : null;
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
}
