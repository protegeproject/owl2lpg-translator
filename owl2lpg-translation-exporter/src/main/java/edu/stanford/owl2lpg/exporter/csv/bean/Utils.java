package edu.stanford.owl2lpg.exporter.csv.bean;

import static java.lang.String.format;

public class Utils {

  public static String NodeID(int nodeId) {
    return format("NodeId_%d", nodeId);
  }
}
