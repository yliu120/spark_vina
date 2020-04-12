package org.spark_vina;

import java.util.List;

public final class VinaTools {
  public static native List<String> readLigandsToStrings(String ligandPath);

  public static native boolean loaded();

  static {
    LibraryLoader.load();
  }
}
