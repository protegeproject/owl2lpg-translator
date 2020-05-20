package edu.stanford.owl2lpg.exporter.csv.beans;

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

  public static PropertylessEdge create(@Nonnull String startNodeId,
                                        @Nonnull String endNodeId,
                                        @Nonnull String edgeType) {
    return new AutoValue_PropertylessEdge(startNodeId, endNodeId, edgeType);
  }

  public static PropertylessEdge of(@Nonnull Edge edge) {
    checkNotNull(edge);
    return create(
        edge.getFromNode().getNodeId().toString(),
        edge.getToNode().getNodeId().toString(),
        edge.getLabel().name());
  }

  @Nonnull
  public abstract String getStartNodeId();

  @Nonnull
  public abstract String getEndNodeId();

  @Nonnull
  public abstract String getEdgeType();
}
