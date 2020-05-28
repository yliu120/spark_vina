package org.spark.tools;

import com.google.common.base.Splitter;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CompoundMetadata implements Serializable {

  private static final long serialVersionUID = 4L;
  private static final Logger LOGGER = LoggerFactory.getLogger(CompoundMetadata.class);

  private String protomerId;
  private String zincId;
  private PhLevel phLevel;
  private double apolarDesolvation;
  private double polarDesolvation;
  private double logP;
  private int hBondDonors;
  private int hBondAcceptors;
  private int rotatableBonds;
  private String smileString;

  public static CompoundMetadata parseFromTSVLine(String line) {
    List<String> components = Splitter.onPattern("\t").trimResults().omitEmptyStrings()
        .splitToList(line);
    return new CompoundMetadata(components.get(0), components.get(1),
        toPhLevelEnum(components.get(2)),
        Double.parseDouble(components.get(3)), Double.parseDouble(components.get(4)),
        Double.parseDouble(components.get(7)), Integer.parseInt(components.get(8)),
        Integer.parseInt(components.get(9)), Integer.parseInt(components.get(10)),
        components.get(11)
    );
  }

  public String getProtomerId() {
    return protomerId;
  }

  public String getZincId() {
    return zincId;
  }

  public PhLevel getPhLevel() {
    return phLevel;
  }

  public double getApolarDesolvation() {
    return apolarDesolvation;
  }

  public double getPolarDesolvation() {
    return polarDesolvation;
  }

  public double getLogP() {
    return logP;
  }

  public int gethBondDonors() {
    return hBondDonors;
  }

  public int gethBondAcceptors() {
    return hBondAcceptors;
  }

  public int getRotatableBonds() {
    return rotatableBonds;
  }

  public String getSmileString() {
    return smileString;
  }

  private CompoundMetadata(String protomerId, String zincId, PhLevel phLevel,
      double apolarDesolvation,
      double polarDesolvation, double logP, int hBondDonors,
      int hBondAcceptors, int rotatableBonds, String smileString) {
    this.protomerId = protomerId;
    this.zincId = zincId;
    this.phLevel = phLevel;
    this.apolarDesolvation = apolarDesolvation;
    this.polarDesolvation = polarDesolvation;
    this.logP = logP;
    this.hBondDonors = hBondDonors;
    this.hBondAcceptors = hBondAcceptors;
    this.rotatableBonds = rotatableBonds;
    this.smileString = smileString;
  }

  private static PhLevel toPhLevelEnum(String phLevel) {
    switch (phLevel) {
      case "ref":
        return PhLevel.REF;
      case "mid":
        return PhLevel.MID;
      case "low":
        return PhLevel.LOW;
      case "high":
        return PhLevel.HIGH;
      default:
        return PhLevel.UNKNOWN;
    }
  }
}
