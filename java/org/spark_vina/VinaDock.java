package org.spark_vina;

import org.spark_vina.SparkVinaProtos.VinaResult;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;
import java.util.stream.Collectors;

public final class VinaDock {

  private final long nativeHandle;
  public VinaDock(
      String receptorPath,
      double centerX,
      double centerY,
      double centerZ,
      double sizeX,
      double sizeY,
      double sizeZ,
      int cpu,
      int numModes) {
    nativeHandle = nativeCreate(
        receptorPath,
        centerX,
        centerY,
        centerZ,
        sizeX,
        sizeY,
        sizeZ,
        cpu,
        numModes);
    if (nativeHandle == 0) {
      throw new RuntimeException("Cannot create native C++ VinaDock object.");
    }
  }
  
  public List<VinaResult> vinaFit(List<String> ligandStrs,
                                  double filterLimit) {
    String[] ligandStringArray = new String[ligandStrs.size()];
    return nativeVinaFit(nativeHandle, ligandStringArray, filterLimit)
        .stream()
        .map(
            nativeResultBytes -> {
              try {
                return VinaResult.parseFrom(nativeResultBytes);
              } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
              }
            })
        .collect(Collectors.toList());
  }
  
  private native long nativeCreate(
      String receptorPath,
      double centerX,
      double doubleY,
      double centerZ,
      double sizeX,
      double sizeY,
      double sizeZ,
      int cpu,
      int numModes);

  private native List<byte[]> nativeVinaFit(
      long nativeHandle,
      String[] ligandStringArray,
      double filterLimit);
      
  private static void loadNativeLibrary() {
    System.loadLibrary("");
  }

  static {
    loadNativeLibrary();
  }
}