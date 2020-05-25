package org.spark.tools;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import org.spark_vina.CompoundProtos.Compound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZincUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(ZincUtils.class);

  public static Optional<Compound> convertMol2StringToPdbqtCompound(String mol2String) {
    try {
      Compound compound = Compound.parseFrom(convertMol2StringToPdbqtCompoundBytes(mol2String));
      if (compound.getOriginalPdbqt().isEmpty()) {
        return Optional.absent();
      }
      return Optional.of(compound);
    } catch (InvalidProtocolBufferException e) {
      LOGGER.error("Unable to parse the returned PDBQT compound: {}", e.toString());
      return Optional.absent();
    }
  }

  private static native byte[] convertMol2StringToPdbqtCompoundBytes(String mol2String);

  static {
    LibraryLoader.load("zinc_utils_jni_all");
  }
}