package edu.stanford.owl2lpg.exporter.csv;

import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.ProjectBranchTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLAxiom;

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
  private final ProjectBranchTranslator projectBranchTranslator;

  @Nonnull
  private final CsvWriter<Node> nodesCsvWriter;

  @Nonnull
  private final CsvWriter<Edge> relationshipsCsvWriter;

  @Nonnull
  private final NodeTracker nodeTracker;

  @Nonnull
  private final EdgeTracker edgeTracker;

  private long nodeCount = 0;

  private long edgeCount = 0;

  private final EnumMap<EdgeLabel, Counter> edgeLabelMultiset = new EnumMap<>(EdgeLabel.class);

  private final EnumMap<NodeLabels, Counter> nodeLabelsMultiset = new EnumMap<>(NodeLabels.class);

  @Inject
  public CsvExporter(@Nonnull OntologyDocumentAxiomTranslator axiomTranslator,
                     @Nonnull ProjectBranchTranslator projectBranchTranslator,
                     @Nonnull CsvWriter<Node> nodesCsvWriter,
                     @Nonnull CsvWriter<Edge> relationshipsCsvWriter,
                     @Nonnull NodeTracker nodeTracker,
                     @Nonnull EdgeTracker edgeTracker) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.projectBranchTranslator = checkNotNull(projectBranchTranslator);
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

  public void write(@Nonnull ProjectId projectId,
                    @Nonnull BranchId branchId,
                    @Nonnull OntologyDocumentId documentId) {
    var translation = projectBranchTranslator.translate(projectId, branchId, documentId);
    writeTranslation(translation);
  }

  public void flush() throws IOException {
    nodesCsvWriter.flush();
    relationshipsCsvWriter.flush();
  }

  private void writeTranslation(Translation translation) {
    var node = translation.getMainNode();
    writeNode(node, canPotentiallyHaveDuplicates(node));
    for (var edge : translation.getEdges()) {
      writeEdge(edge, canPotentiallyHaveDuplicates(edge));
    }
    for (var nestedTranslation : translation.getNestedTranslations()) {
      writeTranslation(nestedTranslation);
    }
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
        NodeLabels.ONTOLOGY_DOCUMENT).anyMatch(nodeLabels::isa);
  }

  private static boolean canPotentiallyHaveDuplicates(Edge edge) {
    var edgeLabel = edge.getLabel();
    return Stream.of(EdgeLabel.ENTITY_IRI,
        EdgeLabel.ENTITY_SIGNATURE,
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
