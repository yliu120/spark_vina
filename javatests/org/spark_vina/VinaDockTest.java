package org.spark_vina;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.spark_vina.SparkVinaProtos.VinaResult;

@RunWith(JUnit4.class)
public final class VinaDockTest {
  private final double centerX = 0.0;
  private final double centerY = 0.0;
  private final double centerZ = 0.0;
  private final double sizeX = 20.0;
  private final double sizeY = 20.0;
  private final double sizeZ = 20.0;
  private final double filterLimit = 1.0;
  private final int numModes = 4;

  public static Matcher<VinaResult> isValidVinaResult() {
    return new TypeSafeMatcher<VinaResult>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("looks valid.");
      }

      @Override
      public boolean matchesSafely(VinaResult vinaResult) {
        return vinaResult.getLigandStr().contains("REMARK") && vinaResult.getAffinity() < 1.0;
      }
    };
  }

  @Test
  public void vinaJniLibsLoaded() {
    assertTrue(VinaTools.loaded());
  }

  @Test
  public void vinaToolsReadLigandsToStrings() {
    List<String> ligandStrings =
        VinaTools.readLigandsToStrings("data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz");
    assertThat(ligandStrings, contains(containsString("REMARK"), containsString("REMARK")));
  }

  @Test
  public void vinaFitDoesReturnValidResult() {
    int numCpus = 4;
    VinaDock docker =
        new VinaDock(
            "data/protein/4ZPH-docking.pdb.pdbqt",
            centerX,
            centerY,
            centerZ,
            sizeX,
            sizeY,
            sizeZ,
            numCpus,
            numModes);
    List<String> ligandStrings =
        VinaTools.readLigandsToStrings("data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz");
    List<VinaResult> affinities = docker.vinaFit(ligandStrings, filterLimit);
    docker.finalize();
    assertThat(affinities, contains(isValidVinaResult(), isValidVinaResult()));
  }

  @Test
  public void vinaFitDoesReturnValidResultDifferentCpuNumber() {
    int numCpus = 2;
    VinaDock docker =
        new VinaDock(
            "data/protein/4ZPH-docking.pdb.pdbqt",
            centerX,
            centerY,
            centerZ,
            sizeX,
            sizeY,
            sizeZ,
            numCpus,
            numModes);
    List<String> ligandStrings =
        VinaTools.readLigandsToStrings("data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz");
    List<VinaResult> affinities = docker.vinaFit(ligandStrings, filterLimit);
    docker.finalize();
    assertThat(affinities, contains(isValidVinaResult(), isValidVinaResult()));
  }
}
