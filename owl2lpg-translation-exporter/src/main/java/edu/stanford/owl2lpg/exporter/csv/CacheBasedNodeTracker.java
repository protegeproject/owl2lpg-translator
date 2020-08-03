package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
public class CacheBasedNodeTracker implements ExportTracker<Node> {

  private static final int DEFAULT_CAPACITY = 1_000_000;

  @Nonnull
  private final LoadingCache<NodeId, String> trackedNodes;

  public CacheBasedNodeTracker() {
    this(DEFAULT_CAPACITY);
  }

  public CacheBasedNodeTracker(int capacity) {
    trackedNodes = CacheBuilder.newBuilder()
        .maximumSize(capacity)
        .build(new CacheLoader<>() {
          @Override
          public String load(@Nonnull NodeId nodeId) {
            return "";
          }
        });
  }

  @Override
  public boolean contains(Node node) {
    var nodeId = node.getNodeId();
    return !Objects.isNull(trackedNodes.getIfPresent(nodeId));
  }

  @Override
  public void add(Node node, Consumer<Node> callback) {
    if (!contains(node)) {
      var nodeId = node.getNodeId();
      trackedNodes.put(nodeId, "");
      callback.accept(node);
    }
  }

  @Override
  public int size() {
    return (int) trackedNodes.size();
  }
}
