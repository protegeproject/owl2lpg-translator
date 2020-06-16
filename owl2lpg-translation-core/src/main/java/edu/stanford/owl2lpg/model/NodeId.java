package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;

/**
 * Represents a construction of a node identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class NodeId {

  public static NodeId create(long idNumber) {
    return new AutoValue_NodeId(idNumber);
  }

  @JsonValue
  public abstract long getId();

  @Override
  public String toString() {
    return "NodeId_" + getId();
  }
}
