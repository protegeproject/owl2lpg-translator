package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CacheBasedEdgeTracker implements ExportTracker<Edge> {

  private static final int DEFAULT_CAPACITY = 1_000_000;

  @Nonnull
  private final LoadingCache<EdgeKey, String> trackedEdges;

  public CacheBasedEdgeTracker() {
    this(DEFAULT_CAPACITY);
  }

  public CacheBasedEdgeTracker(int capacity) {
    trackedEdges = CacheBuilder.newBuilder()
        .maximumSize(capacity)
        .build(new CacheLoader<>() {
          @Override
          public String load(@Nonnull EdgeKey edgeKey) {
            return "";
          }
        });
  }

  @Override
  public boolean contains(Edge edge) {
    var edgeKey = getEdgeKey(edge);
    return !Objects.isNull(trackedEdges.getIfPresent(edgeKey));
  }

  private static EdgeKey getEdgeKey(Edge edge) {
    return EdgeKey.create(
        edge.getStartId(),
        edge.getEndId(),
        edge.getLabel().name());
  }

  @Override
  public void add(Edge edge, Consumer<Edge> callback) {
    if (!contains(edge)) {
      var edgeKey = getEdgeKey(edge);
      trackedEdges.put(edgeKey, "");
      callback.accept(edge);
    }
  }

  @Override
  public int size() {
    return (int) trackedEdges.size();
  }
}
