package org.spark_vina;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.spark_vina.SparkVinaProtos.VinaResult;

@RunWith(JUnit4.class)
public final class VinaDockTest {
  @Test
  public void vinaToolsReadLigandsToStrings() {
    List<String> ligandStrings = VinaTools.readLigandsToStrings(
        "data/ligands/HB/AAMM/HBAAMM.xaa.pdbqt.gz");
    assertEquals(ligandStrings.size(), 2);
  }
  
  @Test
  public void vinaFitDoesReturnValidResult() {
    
  }
}