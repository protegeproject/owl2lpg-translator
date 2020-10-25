package edu.stanford.owl2lpg.exporter.csv.writer;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HashSetNodeTracker implements NodeTracker {

  private static final int DEFAULT_INITIAL_CAPACITY = 1_000_000;

  @Nonnull
  private final Set<NodeId> trackedNodes;

  public HashSetNodeTracker() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public HashSetNodeTracker(int initialCapacity) {
    this.trackedNodes = Sets.newHashSetWithExpectedSize(initialCapacity);
  }

  @Override
  public boolean contains(Node node) {
    return trackedNodes.contains(node.getNodeId());
  }

  /**
   * Performs the callback function when the tracker doesn't contain
   * the given node.
   *
   * @param node     The node to check
   * @param callback A callback function when the cache doesn't contain
   *                 the given node.
   */
  @Override
  public void add(Node node, Consumer<Node> callback) {
    if (trackedNodes.add(node.getNodeId())) {
      callback.accept(node);
    }
  }

  @Override
  public int size() {
    return trackedNodes.size();
  }
}
