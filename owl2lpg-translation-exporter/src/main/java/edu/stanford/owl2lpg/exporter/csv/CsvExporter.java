package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.visitors.OWLLiteral2;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.EnumMap;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CsvExporter {

  @Nonnull
  private final OntologyDocumentAxiomTranslator axiomTranslator;

  @Nonnull
  private final CsvWriter<Node> nodesCsvWriter;

  @Nonnull
  private final CsvWriter<Edge> relationshipsCsvWriter;

  @Nonnull
  private final ExportTracker<Node> nodeTracker;

  @Nonnull
  private final ExportTracker<Edge> edgeTracker;

  private long nodeCount = 0;

  private long edgeCount = 0;

  private final EnumMap<EdgeLabel, Counter> edgeLabelMultiset = new EnumMap<>(EdgeLabel.class);

  private final EnumMap<NodeLabels, Counter> nodeLabelsMultiset = new EnumMap<>(NodeLabels.class);

  @Inject
  public CsvExporter(@Nonnull OntologyDocumentAxiomTranslator axiomTranslator,
                     @Nonnull CsvWriter<Node> nodesCsvWriter,
                     @Nonnull CsvWriter<Edge> relationshipsCsvWriter,
                     @Nonnull ExportTracker<Node> nodeTracker,
                     @Nonnull ExportTracker<Edge> edgeTracker) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.nodesCsvWriter = checkNotNull(nodesCsvWriter);
    this.relationshipsCsvWriter = checkNotNull(relationshipsCsvWriter);
    this.nodeTracker = checkNotNull(nodeTracker);
    this.edgeTracker = checkNotNull(edgeTracker);
    Stream.of(EdgeLabel.values())
        .forEach(v -> edgeLabelMultiset.put(v, new Counter()));
    Stream.of(NodeLabels.values())
        .forEach(v -> nodeLabelsMultiset.put(v, new Counter()));
  }

  public ImmutableMultiset<NodeLabels> getNodeLabelsMultiset() {
    ImmutableMultiset.Builder<NodeLabels> b = ImmutableMultiset.builder();
    nodeLabelsMultiset.forEach((l, c) -> b.setCount(l, c.getValue()));
    return b.build();
  }

  public ImmutableMultiset<EdgeLabel> getEdgeLabelMultiset() {
    ImmutableMultiset.Builder<EdgeLabel> b = ImmutableMultiset.builder();
    edgeLabelMultiset.forEach((l, c) -> b.setCount(l, c.getValue()));
    return b.build();
  }

  public void write(@Nonnull OntologyDocumentId documentId,
                    @Nonnull OWLAxiom axiom) {
    var translation = axiomTranslator.translate(documentId, axiom);
    writeTranslation(translation);
  }

  public void flush() throws IOException {
    nodesCsvWriter.flush();
    relationshipsCsvWriter.flush();
  }

  private void writeTranslation(Translation translation) {
    var mainNode = translation.getMainNode();
    writeNode(mainNode, isPotentialDuplicate(translation));
    for (var edge : translation.getEdges()) {
      writeEdge(edge, isPotentialDuplicate(translation));
    }
    for (var nestedTranslation : translation.getNestedTranslations()) {
      writeTranslation(nestedTranslation);
    }
  }

  private static boolean isPotentialDuplicate(Translation translation) {
    var translatedObject = translation.getTranslatedObject();
    return translatedObject instanceof IRI
        || translatedObject instanceof OWLEntity
        || translatedObject instanceof OWLLiteral2
        || translatedObject instanceof OWLClassExpression
        || translatedObject instanceof OWLObjectPropertyExpression
        || translatedObject instanceof OWLDataPropertyExpression
        || translatedObject instanceof OWLDataRange
        || translatedObject instanceof OWLFacetRestriction
        || translatedObject instanceof OntologyDocumentId;
  }

  private void writeNode(Node node, boolean potentialDuplicate) {
    if (potentialDuplicate) {
      nodeTracker.add(node, this::writeNode);
    } else {
      writeNode(node);
    }
  }

  private void writeNode(Node node) {
    try {
      nodeCount++;
      nodesCsvWriter.write(node);
      nodeLabelsMultiset.get(node.getLabels()).increment();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeEdge(Edge edge, boolean potentialDuplicate) {
    if (potentialDuplicate) {
      edgeTracker.add(edge, this::writeEdge);
    } else {
      writeEdge(edge);
    }
  }

  private void writeEdge(Edge edge) {
    try {
      edgeCount++;
      relationshipsCsvWriter.write(edge);
      edgeLabelMultiset.get(edge.getLabel()).increment();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getNodeCount() {
    return nodeCount;
  }

  public long getTrackedNodeCount() {
    return nodeTracker.size();
  }

  public long getEdgeCount() {
    return edgeCount;
  }

  public long getTrackedEdgeCount() {
    return edgeTracker.size();
  }

  /* A static utility class to do the counting for each translation per node and edge labels */
  private static class Counter {

    private int value = 0;

    public int getValue() {
      return value;
    }

    public void increment() {
      value++;
    }
  }
}
