package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.*;
import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents a graph connector (or an edge) from one node to the other.
 * The edge can have a label and a set of key-value properties that
 * describe the edge.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Edge {


  public static Edge create(@Nonnull Node fromNode,
                            @Nonnull Node toNode,
                            @Nonnull EdgeLabel label,
                            @Nonnull Properties properties) {
    return new AutoValue_Edge(fromNode, toNode, label, properties);
  }

  public static Edge create(@Nonnull Node fromNode,
                            @Nonnull Node toNode,
                            @Nonnull EdgeLabel label) {
    return create(fromNode, toNode, label, Properties.empty());
  }

  @Nullable
  public <E> E getProperty(String key) {
    return getProperties().get(key);
  }

  public String printLabel() {
    return getLabel().printLabel();
  }

  public String printProperties() {
    return getProperties().printProperties();
  }

  @JsonIgnore
  public abstract Node getFromNode();

  @JsonProperty("START_ID")
  public long getStartId() {
    return getFromNode().getNodeId().getId();
  }

  @JsonIgnore
  public abstract Node getToNode();

  @JsonProperty("END_ID")
  public long getEndId() {
    return getToNode().getNodeId().getId();
  }

  @JsonProperty(":LABEL")
  public abstract EdgeLabel getLabel();

  @JsonIgnore
  public abstract Properties getProperties();

  @JsonAnyGetter
  @JsonUnwrapped
  public Map<String, Object> properties() {
    return getProperties().getMap();
  }
}
