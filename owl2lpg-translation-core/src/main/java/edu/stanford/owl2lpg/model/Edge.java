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

  public static final String N4J_JSON_LABELS = ":TYPE";

  public static final String N4J_JSON_START_ID = ":START_ID";

  public static final String N4J_JSON_END_ID = ":END_ID";

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

  public boolean isTypeOf(EdgeLabel edgeLabel) {
    return getLabel().equals(edgeLabel);
  }

  @JsonIgnore
  public abstract Node getFromNode();

  @JsonProperty(N4J_JSON_START_ID)
  public long getStartId() {
    return getFromNode().getNodeId().getId();
  }

  @JsonIgnore
  public abstract Node getToNode();

  @JsonProperty(N4J_JSON_END_ID)
  public long getEndId() {
    return getToNode().getNodeId().getId();
  }

  @JsonProperty(N4J_JSON_LABELS)
  public abstract EdgeLabel getLabel();

  @JsonIgnore
  public abstract Properties getProperties();

  @JsonAnyGetter
  @JsonUnwrapped
  public Map<String, Object> properties() {
    return getProperties().getMap();
  }
}
