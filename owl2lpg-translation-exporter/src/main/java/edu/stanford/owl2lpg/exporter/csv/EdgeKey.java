package edu.stanford.owl2lpg.exporter.csv;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.Comparator;

@AutoValue
public abstract class EdgeKey implements Comparable<EdgeKey> {

  public static EdgeKey create(String startId, String endId, String edgeLabel) {
    return new AutoValue_EdgeKey(startId, endId, edgeLabel);
  }

  public abstract String getStartId();

  public abstract String getEndId();

  public abstract String getEdgeLabel();

  @Override
  public int compareTo(@Nonnull EdgeKey o) {
    return Comparator.comparing(EdgeKey::getStartId)
        .thenComparing(EdgeKey::getEndId)
        .thenComparing(EdgeKey::getEdgeLabel)
        .compare(this, o);
  }
}
