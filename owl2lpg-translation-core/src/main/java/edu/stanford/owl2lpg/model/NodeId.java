package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;
import com.google.common.base.Stopwatch;

import static java.lang.String.format;

/**
 * Represents a construction of a node identifier.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class NodeId {

  public static NodeId create(Object identifierObject) {
    return new AutoValue_NodeId(identifierObject);
  }

  public abstract Object getIdentifierObject();

  @Override
  public String toString() {
    return "NodeId_" + getIdentifierObject();
  }
}
