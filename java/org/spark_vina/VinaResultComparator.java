package org.spark_vina;

import java.io.Serializable;
import java.util.Comparator;
import org.spark_vina.SparkVinaProtos.VinaResult;

public class VinaResultComparator implements Comparator<VinaResult>, Serializable {
  private static final long serialVersionUID = 1;
  /**
   * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer
   * as the first argument is less than, equal to, or greater than the second.<p>
   *
   * @param o1 the first object to be compared.
   * @param o2 the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than,
   * equal to, or greater than the second.
   * @throws NullPointerException if an argument is null and this comparator does not permit null
   * arguments
   * @throws ClassCastException if the arguments' types prevent them from being compared by this
   * comparator.
   */
  @Override
  public int compare(VinaResult o1, VinaResult o2) {
    return (int) (o1.getAffinity() - o2.getAffinity());
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }
}
