package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import static java.lang.String.format;

/**
 * Represents a construction of a node identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class NodeId {

  public static NodeId create(int numberSequence) {
    return new AutoValue_NodeId(numberSequence);
  }

  public abstract int getNumberSequence();

  @Override
  public String toString() {
    return format("NodeId_%d", getNumberSequence());
  }
}
