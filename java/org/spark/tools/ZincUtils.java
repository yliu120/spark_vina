package org.spark.tools;

import com.google.common.base.Optional;
import com.google.protobuf.InvalidProtocolBufferException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

  public static List<String> readAllMol2CompoundsFromFile(String path) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
      StringBuilder mol2StringBuilder = null;
      List<String> mol2Strings = new ArrayList<>();
      for (String line = bufferedReader.readLine(); line != null;
          line = bufferedReader.readLine()) {
        if (line.isEmpty()) {
          continue;
        }
        if (line.contains("@<TRIPOS>MOLECULE")) {
          if (mol2StringBuilder != null) {
            mol2Strings.add(mol2StringBuilder.toString());
          }
          mol2StringBuilder = new StringBuilder(line);
        } else {
          mol2StringBuilder.append(line);
        }
        mol2StringBuilder.append('\n');
      }
      return mol2Strings;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static native byte[] convertMol2StringToPdbqtCompoundBytes(String mol2String);

  static {
    LibraryLoader.load("zinc_utils_jni_all");
  }
}