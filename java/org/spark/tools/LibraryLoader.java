package org.spark.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for loading the Java native library.
 *
 * <p>A portable way to carry the Java native library of Vina around is to pack it inside the Jar
 * file and load it through these steps: 1) Find the library in Jar. 2) Copy it to a temp file. 3)
 * Load the temp file into the program.
 *
 * <p>This approach is inspired by
 * https://github.com/tensorflow/tensorflow/blob/master/tensorflow/java/ and the stackoverflow
 * question https://stackoverflow.com/questions/2937406/. Shamelessly copied some util code from
 * Tensorflow.
 */
public final class LibraryLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(LibraryLoader.class);
  private static ConcurrentMap<String, Boolean> isLibraryLoaded = new ConcurrentHashMap<>();

  public static void load(String jniLibraryName) {
    if (isLibraryLoaded.getOrDefault(jniLibraryName, false) || tryLoadLibrary(jniLibraryName)) {
      return;
    }
    // Native code is not present, perhaps it has been packaged into the .jar file containing this.
    // Extract the JNI library itself
    LOGGER.info("Load native JNI library failed. We then try to load from the standalone JAR.");
    final String jniLibName = System.mapLibraryName(jniLibraryName);
    final String jniResourceName = makeResourceName(jniLibName);
    LOGGER.info("jniResourceName: " + jniResourceName);
    final InputStream jniResource =
        LibraryLoader.class.getClassLoader().getResourceAsStream(jniResourceName);
    if (jniResource == null) {
      throw new UnsatisfiedLinkError(
          String.format(
              "Cannot find native library for OS: %s, architecture: %s.", os(), architecture()));
    }
    try {
      // Create a temporary directory for the extracted resource and its dependencies.
      final File tempPath = createTemporaryDirectory();
      // Deletions are in the reverse order of requests, so we need to request that the directory be
      // deleted first, so that it is empty when the request is fulfilled.
      tempPath.deleteOnExit();
      final String tempDirectory = tempPath.getCanonicalPath();
      System.load(extractResource(jniResource, jniLibName, tempDirectory));
      isLibraryLoaded.put(jniLibraryName, true);
    } catch (IOException e) {
      throw new UnsatisfiedLinkError(
          String.format(
              "Unable to extract native library into a temporary file (%s)", e.toString()));
    }
  }

  private static boolean tryLoadLibrary(String jniLibName) {
    try {
      LOGGER.info("Try to load JNI library: " + jniLibName);
      System.loadLibrary(jniLibName);
      isLibraryLoaded.put(jniLibName, true);
      return true;
    } catch (UnsatisfiedLinkError e) {
      LOGGER.error("tryLoadLibraryFailed: " + e.getMessage());
      return false;
    }
  }

  private static String extractResource(
      InputStream resource, String resourceName, String extractToDirectory) throws IOException {
    final File dst = new File(extractToDirectory, resourceName);
    dst.deleteOnExit();
    final String dstPath = dst.toString();
    LOGGER.info("extracting native library to: " + dstPath);
    final long nbytes = copy(resource, dst);
    LOGGER.info(String.format("copied %d bytes to %s", nbytes, dstPath));
    return dstPath;
  }

  private static String os() {
    final String p = System.getProperty("os.name").toLowerCase();
    if (p.contains("linux")) {
      return "linux";
    } else if (p.contains("os x") || p.contains("darwin")) {
      return "darwin";
    } else if (p.contains("windows")) {
      return "windows";
    } else {
      return p.replaceAll("\\s", "");
    }
  }

  private static String architecture() {
    final String arch = System.getProperty("os.arch").toLowerCase();
    return (arch.equals("amd64")) ? "x86_64" : arch;
  }

  private static String makeResourceName(String baseName) {
    return "jni/" + baseName;
  }

  private static long copy(InputStream src, File dstFile) throws IOException {
    FileOutputStream dst = new FileOutputStream(dstFile);
    try {
      byte[] buffer = new byte[1 << 20]; // 1MB
      long ret = 0;
      int n;
      while ((n = src.read(buffer)) >= 0) {
        dst.write(buffer, 0, n);
        ret += n;
      }
      return ret;
    } finally {
      dst.close();
      src.close();
    }
  }

  private static File createTemporaryDirectory() {
    File baseDirectory = new File(System.getProperty("java.io.tmpdir"));
    String directoryName = "native_libraries-" + System.currentTimeMillis() + "-";
    for (int attempt = 0; attempt < 1000; attempt++) {
      File temporaryDirectory = new File(baseDirectory, directoryName + attempt);
      if (temporaryDirectory.mkdir()) {
        return temporaryDirectory;
      }
    }
    throw new IllegalStateException(
        "Could not create a temporary directory (tried to make "
            + directoryName
            + "*) to extract native libraries.");
  }

  private LibraryLoader() {}
}
