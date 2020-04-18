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
    List<Row> vinaResultRows = new ArrayList<>();

    double sum = 0.0;
    double square_sum = 0.0;
    int count = 0;
    for (VinaResult vinaResult : vinaResults) {
      count++;
      sum += vinaResult.getAffinity();
      square_sum += Math.pow(vinaResult.getAffinity(), 2.0);
      vinaResultRows.add(RowFactory.create(vinaResult.getAffinity(), vinaResult.getLigandStr()));
    }
    this.numModels = count;
    this.affinityMean = sum / (double) this.numModels;
    this.affinityStd = Math.sqrt(square_sum / this.numModels - Math.pow(this.affinityMean, 2.0));
    this.vinaResults = vinaResultRows.toArray(new Row[vinaResultRows.size()]);
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
