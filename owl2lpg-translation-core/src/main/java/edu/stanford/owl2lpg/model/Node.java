package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.*;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents a graph node that has a list of labels and a set of key-value
 * properties to describe the node.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class Node {

  public static final String N4J_JSON_ID = ":ID";

  public static final String N4J_JSON_LABELS = ":LABEL";

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

  @JsonIgnore
  @Nonnull
  public abstract NodeLabels getLabels();

  @JsonProperty(N4J_JSON_LABELS)
  public ImmutableList<String> getLabelsList() {
    return getLabels().asList();
  }

  @JsonIgnore
  @Nonnull
  public abstract Properties getProperties();

  @JsonAnyGetter
  @JsonUnwrapped
  public Map<String, Object> properties() {
    return getProperties().getMap();
  }
}