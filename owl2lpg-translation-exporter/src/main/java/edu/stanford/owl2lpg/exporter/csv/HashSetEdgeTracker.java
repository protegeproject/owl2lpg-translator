package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.EdgeId;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HashSetEdgeTracker implements EdgeTracker {

  private static final int DEFAULT_INITIAL_CAPACITY = 1_000_000;

  @Nonnull
  private final Set<EdgeId> trackedEdges;

  public HashSetEdgeTracker() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public HashSetEdgeTracker(int initialCapacity) {
    this.trackedEdges = Sets.newHashSetWithExpectedSize(initialCapacity);
  }

  @Override
  public boolean contains(Edge edge) {
    return trackedEdges.contains(edge.getEdgeId());
  }

  /**
   * Performs the callback function when the tracker doesn't contain
   * the given edge.
   *
   * @param edge     The edge to check
   * @param callback A callback function when the cache doesn't contain
   *                 the given edge.
   */
  @Override
  public void add(Edge edge, Consumer<Edge> callback) {
    if (trackedEdges.add(edge.getEdgeId())) {
      callback.accept(edge);
    }
  }

  @Override
  public int size() {
    return trackedEdges.size();
  }
}
