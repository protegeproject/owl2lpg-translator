package edu.stanford.owl2lpg.exporter.csv;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;

@AutoValue
public abstract class EdgeKey implements Comparable<EdgeKey> {

  public static EdgeKey create(long startId, long endId, EdgeLabel edgeLabel) {
    return new AutoValue_EdgeKey(startId, endId, edgeLabel);
  }

  public abstract long getStartId();

  public abstract long getEndId();

  public abstract EdgeLabel getEdgeLabel();

  @Override
  public int compareTo(@Nonnull EdgeKey o) {
    long startDiff = o.getStartId() - this.getStartId();
    if (startDiff < 0) {
      return -1;
    } else if (startDiff > 0) {
      return 1;
    }
    long endDiff = o.getEndId() - this.getEndId();
    if (endDiff < 0) {
      return -1;
    } else if (endDiff > 0) {
      return 1;
    }
    return o.getEdgeLabel().compareTo(this.getEdgeLabel());
  }
}
