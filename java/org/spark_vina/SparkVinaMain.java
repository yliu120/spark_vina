package org.spark_vina;

import java.util.ArrayList;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;

public final class SparkVinaMain {
  public static void main(String[] args) throws Exception {
    SparkSession spark = SparkSession
        .builder()
        .appName("SparkVinaMain")
        .getOrCreate();
    spark.stop();
  }
}