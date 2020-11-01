package edu.stanford.owl2lpg.exporter.csv.writer;

import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.EdgeId;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class HashSetEdgeTracker implements EdgeTracker {

  private static final int DEFAULT_INITIAL_CAPACITY = 1_000_000;

  @Nonnull
  private final Set<EdgeId> trackedEdges;

  @Inject
  public HashSetEdgeTracker() {
    this(DEFAULT_INITIAL_CAPACITY);
  }

  public HashSetEdgeTracker(int initialCapacity) {
    this.trackedEdges = Sets.newHashSetWithExpectedSize(initialCapacity);
  }

  @Override
  public void add(Edge edge, Consumer<Edge> writeOp) {
    if (canPotentiallyHaveDuplicates(edge)) {
      var edgeId = edge.getEdgeId();
      if (!trackedEdges.contains(edgeId)) {
        trackedEdges.add(edgeId);
        performWriteOp(edge, writeOp);
      }
    } else {
      performWriteOp(edge, writeOp);
    }
  }

  private void performWriteOp(Edge edge, Consumer<Edge> writeOp) {
    writeOp.accept(edge);
  }

  @Override
  public int size() {
    return trackedEdges.size();
  }

  private static boolean canPotentiallyHaveDuplicates(Edge edge) {
    var edgeLabel = edge.getLabel();
    return Stream.of(EdgeLabel.ENTITY_IRI,
        EdgeLabel.IN_ONTOLOGY_SIGNATURE,
        EdgeLabel.CLASS_EXPRESSION,
        EdgeLabel.OBJECT_PROPERTY_EXPRESSION,
        EdgeLabel.DATA_PROPERTY_EXPRESSION,
        EdgeLabel.OBJECT_PROPERTY,
        EdgeLabel.DATATYPE,
        EdgeLabel.DATA_RANGE,
        EdgeLabel.CONSTRAINING_FACET,
        EdgeLabel.RESTRICTION_VALUE,
        EdgeLabel.RESTRICTION,
        EdgeLabel.INDIVIDUAL,
        EdgeLabel.LITERAL).anyMatch(edgeLabel::isa);
  }
}
