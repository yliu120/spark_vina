package org.spark_vina.util;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spark_vina.CompoundProtos.AtomFeatures;
import org.spark_vina.CompoundProtos.AtomType;
import org.spark_vina.CompoundProtos.Compound;

public final class PdbqtParserHelper {

  private static final String ZINC_PATTERN = "ZINC";
  private static final int ATOM_NAME_INDEX = 2;
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbqtParserHelper.class);
  private static final ImmutableMap<String, AtomType> ATOM_TYPE_MAP;
  private static final ImmutableMap<AtomType, Double> MOLECULAR_WEIGHT_MAP;

  @Deprecated
  public static Optional<String> parseLigandKey(final String ligandString) {
    final String[] lines = ligandString.split("\\r?\\n");
    final java.util.Optional<String> keyLine =
        Arrays.stream(lines).filter(line -> line.contains(ZINC_PATTERN)).findFirst();
    if (!keyLine.isPresent()) {
      return Optional.absent();
    }
    final String[] components = keyLine.get().split(" ");
    final java.util.Optional<String> ligandId =
        Arrays.stream(components).filter(word -> word.contains(ZINC_PATTERN)).findFirst();
    return ligandId.isPresent() ? Optional.of(ligandId.get()) : Optional.absent();
  }

  public static Optional<Compound> parseFeaturesFromPdbqtString(final String ligandString) {
    final Iterable<String> lines =
        Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(ligandString);
    HashMap<String, Integer> atomCountMap = new HashMap<>();
    Splitter atomSplitter = Splitter.on(" ").trimResults().omitEmptyStrings();
    Compound.Builder compoundBuilder = Compound.newBuilder();

    for (final String line : lines) {
      if (line.startsWith("ATOM")) {
        final List<String> atomLineComponents = atomSplitter.splitToList(line);
        final String normalizedAtomType = normalizeAtomType(atomLineComponents.get(ATOM_NAME_INDEX).toUpperCase(),
            atomLineComponents.get(atomLineComponents.size() - 1).toUpperCase());
        final Integer count = atomCountMap.getOrDefault(normalizedAtomType, 0);
        atomCountMap.put(normalizedAtomType, count.intValue() + 1);
      } else if (line.startsWith("REMARK  N")) {
        final List<String> words =
            Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(line);
        final String name = words.get(words.size() - 1);
        if (!name.startsWith("ZINC")) {
          LOGGER.error("Abandon compound with no ID: {}", ligandString);
          return Optional.absent();
        }
        compoundBuilder.setName(name);
      }
    }

    double molecularWeight = 0.0;
    for (final Map.Entry<String, Integer> entry : atomCountMap.entrySet()) {
      final AtomFeatures.Builder builder = compoundBuilder.addAtomFeaturesBuilder();
      final AtomType atomType = ATOM_TYPE_MAP.getOrDefault(entry.getKey(), AtomType.UNKNOWN);
      builder.setAtomType(atomType);
      builder.setCount(entry.getValue());

      if (atomType.equals(AtomType.UNKNOWN)) {
        LOGGER.error("Unknown atom name: {} in compound {}.", entry.getKey(),
            ligandString);
      }
      molecularWeight += MOLECULAR_WEIGHT_MAP.get(atomType) * entry.getValue();
    }

    compoundBuilder.setOriginalPdbqt(ligandString);
    compoundBuilder.setMolecularWeightMillis((int) (molecularWeight * 1000));
    return Optional.of(compoundBuilder.build());
  }

  // Heuristics to speed up parsing.
  private static String normalizeAtomType(String atomName, String atomType) {
    if (atomName.equals(atomType) && ATOM_TYPE_MAP.containsKey(atomName)) {
      return atomName;
    }
    return atomName.substring(0, 1);
  }

  static {
    ATOM_TYPE_MAP =
        new ImmutableMap.Builder<String, AtomType>()
            .put("H", AtomType.HYDROGEN)
            .put("C", AtomType.CARBON)
            .put("N", AtomType.NITROGEN)
            .put("O", AtomType.OXYGEN)
            .put("F", AtomType.FLUROINE)
            .put("NA", AtomType.SODIUM)
            .put("MG", AtomType.MAGNESIUM)
            .put("P", AtomType.PHOSPHORUS)
            .put("S", AtomType.SULFUR)
            .put("K", AtomType.POTASSIUM)
            .put("CA", AtomType.CALCIUM)
            .put("CL", AtomType.CHLORINE)
            .put("BR", AtomType.BROMINE)
            .put("I", AtomType.IODINE)
            .build();

    MOLECULAR_WEIGHT_MAP =
        new ImmutableMap.Builder<AtomType, Double>()
            .put(AtomType.UNKNOWN, 0.0)
            .put(AtomType.HYDROGEN, 1.008)
            .put(AtomType.CARBON, 12.011)
            .put(AtomType.NITROGEN, 14.007)
            .put(AtomType.OXYGEN, 16.0)
            .put(AtomType.FLUROINE, 18.998)
            .put(AtomType.SODIUM, 22.990)
            .put(AtomType.MAGNESIUM, 24.305)
            .put(AtomType.PHOSPHORUS, 30.974)
            .put(AtomType.SULFUR, 32.06)
            .put(AtomType.POTASSIUM, 39.098)
            .put(AtomType.CALCIUM, 40.078)
            .put(AtomType.CHLORINE, 35.45)
            .put(AtomType.BROMINE, 79.904)
            .put(AtomType.IODINE, 126.90)
            .build();
  }
}
