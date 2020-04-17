package edu.stanford.owl2lpg.exporter.csv.beans;

import static java.lang.String.format;

/* package */ class Utils {

  static String NodeID(int nodeId) {
    return format("NodeId_%d", nodeId);
  }
}
