package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
public class SetBasedEdgeTracker implements ExportTracker<Edge> {

  private static final int DEFAULT_INITIAL_CAPACITY = 1_000_000;

  @Nonnull
  private final Set<EdgeKey> trackedEdges;

  public SetBasedEdgeTracker() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public SetBasedEdgeTracker(int initialCapacity) {
    this.trackedEdges = Sets.newHashSetWithExpectedSize(initialCapacity);
  }

  @Override
  public boolean contains(Edge edge) {
    var edgeKey = getEdgeKey(edge);
    return trackedEdges.contains(edgeKey);
  }

  private static EdgeKey getEdgeKey(Edge edge) {
    return EdgeKey.create(
        edge.getStartId(),
        edge.getEndId(),
        edge.getLabel().name());
  }

  /**
   * Performs the callback function when the cache doesn't contain
   * the given edge.
   *
   * @param edge     The edge to check
   * @param callback A callback function when the cache doesn't contain
   *                 the given edge.
   */
  @Override
  public void add(Edge edge, Consumer<Edge> callback) {
    var edgeKey = getEdgeKey(edge);
    if (trackedEdges.add(edgeKey)) {
      callback.accept(edge);
    }
  }

  @Override
  public int size() {
    return trackedEdges.size();
  }
}
