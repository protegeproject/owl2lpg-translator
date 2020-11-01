package edu.stanford.owl2lpg.exporter.csv.writer;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HashSetNodeTracker implements NodeTracker {

  private static final int DEFAULT_INITIAL_CAPACITY = 1_000_000;

  @Nonnull
  private final Set<NodeId> trackedNodes;

  @Inject
  public HashSetNodeTracker() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public HashSetNodeTracker(int initialCapacity) {
    this.trackedNodes = Sets.newHashSetWithExpectedSize(initialCapacity);
  }

  @Override
  public void add(@Nonnull Node node, @Nonnull Consumer<Node> writeOp) {
    if (canPotentiallyHaveDuplicates(node)) {
      var nodeId = node.getNodeId();
      if (!trackedNodes.contains(nodeId)) {
        trackedNodes.add(nodeId);
        performWriteOp(node, writeOp);
      }
    } else {
      performWriteOp(node, writeOp);
    }
  }

  private void performWriteOp(Node node, Consumer<Node> writeOp) {
    writeOp.accept(node);
  }

  @Override
  public int size() {
    return trackedNodes.size();
  }

  private static boolean canPotentiallyHaveDuplicates(Node node) {
    var nodeLabels = node.getLabels();
    return Stream.of(NodeLabels.IRI,
        NodeLabels.ENTITY,
        NodeLabels.CLASS_EXPRESSION,
        NodeLabels.OBJECT_PROPERTY_EXPRESSION,
        NodeLabels.DATA_PROPERTY_EXPRESSION,
        NodeLabels.DATA_RANGE,
        NodeLabels.FACET_RESTRICTION,
        NodeLabels.LITERAL,
        NodeLabels.ONTOLOGY_DOCUMENT,
        NodeLabels.PROPERTY_CHAIN).anyMatch(nodeLabels::isa);
  }
}
