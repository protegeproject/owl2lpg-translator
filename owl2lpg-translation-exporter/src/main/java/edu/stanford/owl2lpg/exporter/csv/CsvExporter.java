package edu.stanford.owl2lpg.exporter.csv;

import com.carrotsearch.hppcrt.sets.LongHashSet;
import com.google.common.collect.ImmutableMultiset;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.TranslationSessionUniqueEncounterNodeChecker;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
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
  private TranslationSessionUniqueEncounterNodeChecker translationSessionUniqueEncounterNodeChecker;

  @Nonnull
  private final LongHashSet exportedNodes = new LongHashSet(1_000_000);

  @Nonnull
  private final Set<EdgeKey> writtenNodeEdges = new HashSet<>(1_000_000);

  private long nodeCount = 0;

  private long edgeCount = 0;

  private final EnumMap<EdgeLabel, Counter> edgeLabelMultiset = new EnumMap<>(EdgeLabel.class);

  private final EnumMap<NodeLabels, Counter> nodeLabelsMultiset = new EnumMap<>(NodeLabels.class);

  @Inject
  public CsvExporter(@Nonnull OntologyDocumentAxiomTranslator axiomTranslator,
                     @Nonnull CsvWriter<Node> nodesCsvWriter,
                     @Nonnull CsvWriter<Edge> relationshipsCsvWriter,
                     @Nonnull TranslationSessionUniqueEncounterNodeChecker translationSessionUniqueEncounterNodeChecker) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.nodesCsvWriter = checkNotNull(nodesCsvWriter);
    this.relationshipsCsvWriter = checkNotNull(relationshipsCsvWriter);
    this.translationSessionUniqueEncounterNodeChecker = checkNotNull(translationSessionUniqueEncounterNodeChecker);
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
                    @Nonnull OWLAxiom axiom) throws IOException {
    var translation = axiomTranslator.translate(documentId, axiom);
    writeTranslation(translation);
  }

  public void flush() throws IOException {
    nodesCsvWriter.flush();
    relationshipsCsvWriter.flush();
  }

  private void writeTranslation(Translation translation) throws IOException {
    writeNode(translation.getMainNode(), translationSessionUniqueEncounterNodeChecker.isTranslationSessionUniqueEncounterNodeObject(translation.getTranslatedObject()));
    for(var edge : translation.getEdges()) {
      writeEdge(edge, isPotentialDuplicateEdge(translation));
    }
    for(var t : translation.getNestedTranslations()) {
      writeTranslation(t);
    }
  }

  private static boolean isPotentialDuplicateEdge(Translation t) {
    // We only visit axioms once in a session
    // Since we only visit axioms once, the edges from axioms will only be written once
    // and the edge from an ontology document node to the axiom will only be written once
    return !((t.getTranslatedObject() instanceof OWLAxiom)
            || t.getTranslatedObject() instanceof OntologyDocumentId);
  }

  private void writeEdge(Edge edge, boolean potentialDuplicate) throws IOException {
    if (potentialDuplicate) {
      if(writtenNodeEdges.add(EdgeKey.get(edge.getStartId(),
                                          edge.getEndId(),
                                          edge.getLabel()))) {
        writeEdge(edge);
      }
    }
    else {
      writeEdge(edge);
    }

  }

  private void writeEdge(Edge edge) throws IOException {
    edgeCount++;
    relationshipsCsvWriter.write(edge);
    edgeLabelMultiset.get(edge.getLabel()).increment();
  }

  private boolean writeNode(Node node, boolean unique) throws IOException {
    if (!unique || exportedNodes.add(node.getNodeId().getId())) {
      nodeCount++;
      nodesCsvWriter.write(node);
      nodeLabelsMultiset.get(node.getLabels()).increment();
      return true;
    }
    return false;
  }

  public long getNodeCount() {
    return nodeCount;
  }

  public long getEdgeCount() {
    return edgeCount;
  }


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
