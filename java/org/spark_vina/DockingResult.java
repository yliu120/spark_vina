package org.spark_vina;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.spark_vina.SparkVinaProtos.VinaResult;

public final class DockingResult implements Serializable {
  private static final long serialVersionUID = 1;

  public DockingResult(String compoundKey, Iterable<VinaResult> vinaResults) {
    this.compoundKey = compoundKey;
    List<Row> vinaResultsRows = new ArrayList<>();

    double sum = 0.0;
    double square_sum = 0.0;
    int count = 0;
    for (VinaResult vinaResult : vinaResults) {
      List<Row> vinaResultRow = new ArrayList<>();
      for (VinaResult.Model model : vinaResult.getModelsList()) {
        count++;
        sum += model.getAffinity();
        square_sum += Math.pow(model.getAffinity(), 2.0);
        vinaResultRow.add(RowFactory.create(model.getAffinity(), model.getDockedPdbqt()));
      }
      vinaResultsRows.add(
          RowFactory.create(
              vinaResult.getRandomSeed(), vinaResultRow.toArray(new Row[vinaResultRow.size()])));
    }
    this.numModels = count;
    this.affinityMean = sum / (double) this.numModels;
    this.affinityStd = Math.sqrt(square_sum / this.numModels - Math.pow(this.affinityMean, 2.0));
    this.vinaResults = vinaResultsRows.toArray(new Row[vinaResultsRows.size()]);
  }

  public String getCompoundKey() {
    return compoundKey;
  }

  public double getAffinityMean() {
    return affinityMean;
  }

  public double getAffinityStd() {
    return affinityStd;
  }

  public int getNumModels() {
    return numModels;
  }

  public Row[] getVinaResults() {
    return vinaResults;
  }

  private final String compoundKey;
  private final double affinityMean;
  private final double affinityStd;
  private final int numModels;
  private final Row[] vinaResults;
}
