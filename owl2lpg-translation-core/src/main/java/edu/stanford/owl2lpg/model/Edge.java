package edu.stanford.owl2lpg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a graph connector (or an edge) from one node to the other.
 * The edge can have a label and a set of key-value properties that
 * describe the edge.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
@JsonTypeName("relationship")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
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

  @JsonProperty("start")
  public abstract Node getFromNode();

  @JsonProperty("end")
  public abstract Node getToNode();

  @JsonProperty("label")
  public abstract EdgeLabel getLabel();

  @JsonProperty("properties")
  public abstract Properties getProperties();
}
