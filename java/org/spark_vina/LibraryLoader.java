package org.spark_vina;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
final class LibraryLoader {
  private static final boolean DEBUG =
      System.getProperty("org.spark_vina.LibraryLoader.DEBUG") != null;
  private static final String JNI_LIBNAME = "vina_jni_all";

  public static void load() {
    if (isLoaded() || tryLoadLibrary()) {
      return;
    }
    // Native code is not present, perhaps it has been packaged into the .jar file containing this.
    // Extract the JNI library itself
    final String jniLibName = System.mapLibraryName(JNI_LIBNAME);
    final String jniResourceName = makeResourceName(jniLibName);
    log("jniResourceName: " + jniResourceName);
    final InputStream jniResource =
        LibraryLoader.class.getClassLoader().getResourceAsStream(jniResourceName);
    // Extract the JNI's dependency
    final String frameworkLibName = getVersionedLibraryName(System.mapLibraryName("vina"));
    final String frameworkResourceName = makeResourceName(frameworkLibName);
    log("frameworkResourceName: " + frameworkResourceName);
    final InputStream frameworkResource =
        LibraryLoader.class.getClassLoader().getResourceAsStream(frameworkResourceName);
    if (jniResource == null) {
      throw new UnsatisfiedLinkError(
          String.format(
              "Cannot find native library for OS: %s, architecture: %s. See "
                  + " org.spark_vina.LibraryLoader.DEBUG=1 to the system properties of the JVM.",
              os(), architecture()));
    }
    try {
      // Create a temporary directory for the extracted resource and its dependencies.
      final File tempPath = createTemporaryDirectory();
      // Deletions are in the reverse order of requests, so we need to request that the directory be
      // deleted first, so that it is empty when the request is fulfilled.
      tempPath.deleteOnExit();
      final String tempDirectory = tempPath.getCanonicalPath();
      if (frameworkResource != null) {
        extractResource(frameworkResource, frameworkLibName, tempDirectory);
      } else {
        log(
            frameworkResourceName
                + " not found. This is fine assuming "
                + jniResourceName
                + " is not built to depend on it.");
      }
      System.load(extractResource(jniResource, jniLibName, tempDirectory));
    } catch (IOException e) {
      throw new UnsatisfiedLinkError(
          String.format(
              "Unable to extract native library into a temporary file (%s)", e.toString()));
    }
  }

  private static boolean tryLoadLibrary() {
    try {
      System.loadLibrary(JNI_LIBNAME);
      return true;
    } catch (UnsatisfiedLinkError e) {
      log("tryLoadLibraryFailed: " + e.getMessage());
      return false;
    }
  }

  private static boolean isLoaded() {
    try {
      if (VinaTools.loaded()) {
        log("isLoaded: true");
        return true;
      }
    } catch (UnsatisfiedLinkError e) {
      return false;
    }
    return false;
  }

  private static boolean resourceExists(String baseName) {
    return LibraryLoader.class.getClassLoader().getResource(makeResourceName(baseName)) != null;
  }

  private static String getVersionedLibraryName(String libFilename) {
    final String versionName = getMajorVersionNumber();

    // If we're on darwin, the versioned libraries look like blah.1.dylib.
    final String darwinSuffix = ".dylib";
    if (libFilename.endsWith(darwinSuffix)) {
      final String prefix = libFilename.substring(0, libFilename.length() - darwinSuffix.length());
      if (versionName != null) {
        final String darwinVersionedLibrary = prefix + "." + versionName + darwinSuffix;
        if (resourceExists(darwinVersionedLibrary)) {
          return darwinVersionedLibrary;
        }
      } else {
        // If we're here, we're on darwin, but we couldn't figure out the major version number. We
        // already tried the library name without any changes, but let's do one final try for the
        // library with a .so suffix.
        final String darwinSoName = prefix + ".so";
        if (resourceExists(darwinSoName)) {
          return darwinSoName;
        }
      }
    } else if (libFilename.endsWith(".so")) {
      // Libraries ending in ".so" are versioned like "libfoo.so.1", so try that.
      final String versionedSoName = libFilename + "." + versionName;
      if (versionName != null && resourceExists(versionedSoName)) {
        return versionedSoName;
      }
    }

    // Otherwise, we've got no idea.
    return libFilename;
  }

  /**
   * Returns the major version number of this TensorFlow Java API, or {@code null} if it cannot be
   * determined.
   */
  private static String getMajorVersionNumber() {
    InputStream resourceStream =
        LibraryLoader.class.getClassLoader().getResourceAsStream("tensorflow-version-info");
    if (resourceStream == null) {
      return null;
    }

    try {
      Properties props = new Properties();
      props.load(resourceStream);
      String version = props.getProperty("version");
      // expecting a string like 1.14.0, we want to get the first '1'.
      int dotIndex;
      if (version == null || (dotIndex = version.indexOf('.')) == -1) {
        return null;
      }
      String majorVersion = version.substring(0, dotIndex);
      try {
        Integer.parseInt(majorVersion);
        return majorVersion;
      } catch (NumberFormatException unused) {
        return null;
      }
    } catch (IOException e) {
      log("failed to load tensorflow version info.");
      return null;
    }
  }

  private static String extractResource(
      InputStream resource, String resourceName, String extractToDirectory) throws IOException {
    final File dst = new File(extractToDirectory, resourceName);
    dst.deleteOnExit();
    final String dstPath = dst.toString();
    log("extracting native library to: " + dstPath);
    final long nbytes = copy(resource, dst);
    log(String.format("copied %d bytes to %s", nbytes, dstPath));
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

  private static void log(String msg) {
    if (DEBUG) {
      System.err.println("org.spark_vina: " + msg);
    }
  }

  private static String makeResourceName(String baseName) {
    return "jni/" + String.format("%s-%s/", os(), architecture()) + baseName;
  }

  private static long copy(InputStream src, File dstFile) throws IOException {
    FileOutputStream dst = new FileOutputStream(dstFile);
    try {
      byte[] buffer = new byte[1 << 20]; // 1MB
      long ret = 0;
      int n = 0;
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
    String directoryName = "tensorflow_native_libraries-" + System.currentTimeMillis() + "-";
    for (int attempt = 0; attempt < 1000; attempt++) {
      File temporaryDirectory = new File(baseDirectory, directoryName + attempt);
      if (temporaryDirectory.mkdir()) {
        return temporaryDirectory;
      }
    }
    throw new IllegalStateException(
        "Could not create a temporary directory (tried to make "
            + directoryName
            + "*) to extract TensorFlow native libraries.");
  }

  private LibraryLoader() {}
}
