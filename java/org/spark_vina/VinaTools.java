package org.spark_vina;

import java.util.List;
import org.spark.tools.LibraryLoader;

public final class VinaTools {
  public static native List<String> readLigandsToStrings(String ligandPath);

  static {
    LibraryLoader.load("vina_jni_all");
  }
}
