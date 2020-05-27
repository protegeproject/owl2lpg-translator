package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a graph node that has a list of labels and a set of key-value
 * properties to describe the node.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
@JsonTypeName("node")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = Node.N4J_JSON_TYPE)
public abstract class Node {

  private static final String N4J_JSON_ID = "id";

  private static final String N4J_JSON_LABELS = "labels";

  private static final String N4J_JSON_PROPERTIES = "properties";

  static final String N4J_JSON_TYPE = "type";

  @Nonnull
  public static Node create(@Nonnull NodeId nodeId,
                            @Nonnull NodeLabels labels,
                            @Nonnull Properties properties) {
    return new AutoValue_Node(nodeId, labels, properties);
  }

  @Nonnull
  public static Node create(@Nonnull NodeId nodeId,
                            @Nonnull NodeLabels labels) {
    return create(nodeId, labels, Properties.empty());
  }

  @JsonIgnore
  public boolean isTypeOf(NodeLabels nodeLabels) {
    return getLabels().isa(nodeLabels);
  }

  @Nullable
  public <E> E getProperty(String key) {
    return getProperties().get(key);
  }

  public String printNodeId() {
    return getNodeId().toString();
  }

  public String printLabels() {
    return getLabels().printLabels();
  }

  public String printProperties() {
    return getProperties().printProperties();
  }

  @JsonProperty(N4J_JSON_ID)
  @Nonnull
  public abstract NodeId getNodeId();

  @JsonProperty(N4J_JSON_LABELS)
  @Nonnull
  public abstract NodeLabels getLabels();

  @JsonProperty(N4J_JSON_PROPERTIES)
  @Nonnull
  public abstract Properties getProperties();
}