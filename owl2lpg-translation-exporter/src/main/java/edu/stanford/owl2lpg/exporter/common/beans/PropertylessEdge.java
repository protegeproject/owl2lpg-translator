package edu.stanford.owl2lpg.exporter.common.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class PropertylessEdge {

  public static final String START_NODE_ID = ":START_ID";

  public static final String END_NODE_ID = ":END_ID";

  public static final String EDGE_TYPE = ":TYPE";

  @JsonCreator
  @Nonnull
  public static PropertylessEdge create(@JsonProperty(START_NODE_ID) @Nonnull String startNodeId,
                                        @JsonProperty(END_NODE_ID) @Nonnull String endNodeId,
                                        @JsonProperty(EDGE_TYPE) @Nonnull String edgeType) {
    return new AutoValue_PropertylessEdge(startNodeId, endNodeId, edgeType);
  }

  @Nonnull
  public static PropertylessEdge of(@Nonnull Edge edge) {
    checkNotNull(edge);
    return create(
        edge.getFromNode().getNodeId().toString(),
        edge.getToNode().getNodeId().toString(),
        edge.getLabel().name());
  }

  @JsonProperty(START_NODE_ID)
  @Nonnull
  public abstract String getStartNodeId();

  @JsonProperty(END_NODE_ID)
  @Nonnull
  public abstract String getEndNodeId();

  @JsonProperty(EDGE_TYPE)
  @Nonnull
  public abstract String getEdgeType();
}
