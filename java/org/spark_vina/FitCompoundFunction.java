package org.spark_vina;

import com.google.common.base.Optional;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.SparkVinaProtos.VinaResult;

public class FitCompoundFunction implements Function<String, Optional<VinaResult>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(FitCompoundFunction.class);

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
    return vinaResult;
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
