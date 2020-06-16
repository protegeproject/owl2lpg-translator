package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CacheBasedCsvExportChecker implements CsvExportChecker {

  private final static int DEFAULT_CAPACITY = 1_000_000;

  @Nonnull
  private final LoadingCache<NodeId, String> nodeCache;

  @Nonnull
  private final LoadingCache<EdgeKey, String> edgeCache;

  public CacheBasedCsvExportChecker() {
    this(DEFAULT_CAPACITY);
  }

  public CacheBasedCsvExportChecker(int capacity) {
    this.nodeCache = CacheBuilder.newBuilder()
        .maximumSize(capacity)
        .build(new CacheLoader<NodeId, String>() {
          @Override
          public String load(NodeId nodeId) throws Exception {
            return "";
          }
        });
    this.edgeCache = CacheBuilder.newBuilder()
        .maximumSize(capacity)
        .build(new CacheLoader<EdgeKey, String>() {
          @Override
          public String load(EdgeKey edgeKey) throws Exception {
            return "";
          }
        });
  }

  @Override
  public boolean isExported(Node node) {
    if (nodeCache.getIfPresent(node.getNodeId()) == null) {
      nodeCache.put(node.getNodeId(), "");
      return false;
    } else {
      return true;
    }
  }

  @Override
  public boolean isExported(Edge edge) {
    var edgeKey = EdgeKey.create(edge.getStartId(), edge.getEndId(), edge.getLabel());
    if (edgeCache.getIfPresent(edgeKey) == null) {
      edgeCache.put(edgeKey, "");
      return false;
    } else {
      return true;
    }
  }
}
