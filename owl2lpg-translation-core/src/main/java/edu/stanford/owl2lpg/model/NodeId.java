package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonValue;
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

  public static NodeId create(long identifierObject) {
    return new AutoValue_NodeId(identifierObject);
  }

  @JsonValue
  public abstract long getId();

  @Override
  public String toString() {
    return "NodeId_" + getId();
  }
}
