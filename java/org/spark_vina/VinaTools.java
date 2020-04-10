package org.spark_vina;

import java.util.List;

public final class VinaTools {
  public native static List<String> readLigandsToStrings(String ligandPath);
  private static void loadNativeLibrary() {
    String osName = System.getProperty("os.name").toLowerCase();
    StringBuilder libraryPath = new StringBuilder("jni/libvina_tools_jni");
    // We only support Mac and Linux OS.
    if (osName.contains("mac")) {
      libraryPath.append(".dylib");
    } else {
      libraryPath.append(".so");
    }
    System.loadLibrary(libraryPath.toString());
  }
  static {
    loadNativeLibrary();
  }
}