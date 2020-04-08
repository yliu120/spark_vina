package org.spark_vina;

import org.spark_vina.SparkVinaProtos.VinaResult;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;
import java.util.stream.Collectors;

public final class VinaDock {

  private final long nativeHandle;
  public VinaDock(
      String receptor_path,
      double center_x,
      double center_y,
      double center_z,
      double size_x,
      double size_y,
      double size_z,
      int cpu,
      int num_modes) {
    nativeHandle = nativeCreate(
        receptor_path,
        center_x,
        center_y,
        center_z,
        size_x,
        size_y,
        size_z,
        cpu,
        num_modes);
    if (nativeHandle == 0) {
      throw new RuntimeException("Cannot create native C++ VinaDock object.");
    }
  }
  
  public List<VinaResult> vinaFit(List<String> ligand_strs,
                                  double filter_limit) {
    return nativeVinaFit(nativeHandle, ligand_strs, filter_limit)
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
      String receptor_path,
      double center_x,
      double double_y,
      double center_z,
      double size_x,
      double size_y,
      double size_z,
      int cpu,
      int num_modes);

  private native List<byte[]> nativeVinaFit(
      long nativeHandle,
      List<String> ligand_strs,
      double filter_limit);
      
  private static void loadNativeLibrary() {
    System.loadLibrary("");
  }

  static {
    loadNativeLibrary();
  }
}