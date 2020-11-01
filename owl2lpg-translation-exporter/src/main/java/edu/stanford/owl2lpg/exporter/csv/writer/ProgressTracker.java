package edu.stanford.owl2lpg.exporter.csv.writer;

import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProgressTracker {

  @Nonnull
  private final EnumMap<NodeLabels, Counter> nodeLabelsMultiset = new EnumMap<>(NodeLabels.class);
  
  @Nonnull
  private final EnumMap<EdgeLabel, Counter> edgeLabelMultiset = new EnumMap<>(EdgeLabel.class);

  private long nodeCount = 0;

  private long edgeCount = 0;

  @Inject
  public ProgressTracker() {
    Stream.of(NodeLabels.values()).forEach(v -> nodeLabelsMultiset.put(v, new Counter()));
    Stream.of(EdgeLabel.values()).forEach(v -> edgeLabelMultiset.put(v, new Counter()));
  }

  public long increaseCount(@Nonnull Node node) {
    nodeCount++;
    nodeLabelsMultiset.get(node.getLabels()).increment();
    return nodeCount;
  }

  public long increaseCount(Edge edge) {
    edgeCount++;
    edgeLabelMultiset.get(edge.getLabel()).increment();
    return edgeCount;
  }

  public long getNodeCount() {
    return nodeCount;
  }

  public long getEdgeCount() {
    return edgeCount;
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

  /* A static utility class to do the counting for each Translation object */
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
