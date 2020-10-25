package edu.stanford.owl2lpg.exporter.csv.writer;

import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jCsvWriter {

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
  public Neo4jCsvWriter(@Nonnull CsvWriter<Node> nodesCsvWriter,
                        @Nonnull CsvWriter<Edge> edgeCsvWriter,
                        @Nonnull NodeTracker nodeTracker,
                        @Nonnull EdgeTracker edgeTracker) {
    this.nodesCsvWriter = nodesCsvWriter;
    this.relationshipsCsvWriter = edgeCsvWriter;
    this.nodeTracker = nodeTracker;
    this.edgeTracker = edgeTracker;
    Stream.of(EdgeLabel.values())
        .forEach(v -> edgeLabelMultiset.put(v, new Counter()));
    Stream.of(NodeLabels.values())
        .forEach(v -> nodeLabelsMultiset.put(v, new Counter()));
  }

  public void writeTranslation(Translation translation) {
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
        NodeLabels.ONTOLOGY_DOCUMENT,
        NodeLabels.PROPERTY_CHAIN).anyMatch(nodeLabels::isa);
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
      nodesCsvWriter.flush();
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
      relationshipsCsvWriter.flush();
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

  public void flush() throws IOException {
    nodesCsvWriter.flush();
    relationshipsCsvWriter.flush();
  }

  public void printReport() {
    var console = new PrintWriter(System.out);
    console.printf("\nNodes: %,d\n\n", getNodeCount());
    getNodeLabelsMultiset().forEachEntry((nodeLabels, count) ->
        console.printf("    Node   %-60s %,10d\n", nodeLabels.toNeo4jLabel(), count));
    console.printf("\nRelationships: %,d\n\n", getEdgeCount());
    getEdgeLabelMultiset().forEachEntry((edgeLabel, count) ->
        console.printf("    Rel    %-36s %,10d\n", edgeLabel.toNeo4jLabel(), count));
    console.flush();
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
