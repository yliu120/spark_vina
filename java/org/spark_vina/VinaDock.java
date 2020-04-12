package org.spark_vina;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import java.util.stream.Collectors;
import org.spark_vina.SparkVinaProtos.VinaResult;

/**
 *
 *
 * <h1>Java API of the VinaDock C++ class
 *
 * <p>This class wraps the C++ VinaDock class through JNI.
 *
 * @author Yunlong Liu
 * @version 0.1
 * @since 2020-04-08
 */
public final class VinaDock {

  private final long nativeHandle;

  /**
   * The constructor of the VinaDock class building a docking instance. The docking instance
   * consists of
   *
   * <ul>
   *   <li>receptor PDBQT
   *   <li>A 3D box specifying the region to place the small molecule in.
   * </ul>
   *
   * @param centerX The X Coordinate of the center of the box
   * @param centerY The Y Coordinate of the center of the box
   * @param centerZ The X Coordinate of the center of the box
   * @param sizeX The size on X dimension
   * @param sizeY The size on Y dimension
   * @param sizeZ The size on Z dimension
   * @param cpu The number of cpus used for this docking
   * @param numModes The number of modes returned.
   * @return a VinaDock instance.
   */
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
    nativeHandle =
        nativeCreate(receptorPath, centerX, centerY, centerZ, sizeX, sizeY, sizeZ, cpu, numModes);
    if (nativeHandle == 0) {
      throw new RuntimeException("Cannot create native C++ VinaDock object.");
    }
  }

  /**
   * This method docks a list of ligands with a specified filtering limit on the estimated binding
   * constant.
   *
   * @param ligandStrs A list of ligand PDBQT strings.
   * @param filterLimit A filtering limit.
   * @return List<VinaResult> A list of docking result.
   */
  public List<VinaResult> vinaFit(List<String> ligandStrs, double filterLimit) {
    return nativeVinaFit(nativeHandle, ligandStrs.toArray(String[]::new), filterLimit).stream()
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

  /**
   * Call this method to finalize (destruct) a VinaDock instance. This method must be called
   * explicitly when the instance is no longer used.
   */
  @Override
  protected void finalize() throws Throwable {
    nativeDelete(nativeHandle);
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

  private native void nativeDelete(long nativeHandle);

  private native List<byte[]> nativeVinaFit(
      long nativeHandle, String[] ligandStringArray, double filterLimit);

  static {
    LibraryLoader.load();
  }
}
