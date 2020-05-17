package org.spark_vina.util;

import com.google.common.collect.ImmutableMap;

public final class ZincHelper {
  private static final ImmutableMap<Character, Double> LOGP_MAP;

  public static double convertAlphabetToLogp(Character alpha) {
    // Returns the impossible largest to the user if not found.
    return LOGP_MAP.getOrDefault(alpha, 6.0);
  }

  static {
    LOGP_MAP =
        new ImmutableMap.Builder<Character, Double>()
            .put('A', -1.0)
            .put('B', 0.0)
            .put('C', 1.0)
            .put('D', 2.0)
            .put('E', 2.5)
            .put('F', 3.0)
            .put('G', 3.5)
            .put('H', 4.0)
            .put('I', 4.5)
            .put('J', 5.0)
            .build();
  }
}
