package edu.stanford.owl2lpg.exporter.csv;

import com.carrotsearch.hppcrt.sets.LongHashSet;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
  private final LongHashSet exportedNodes = new LongHashSet(1_000_000);

  @Nonnull
  private final Set<EdgeKey> writtenNodeEdges = new HashSet<>(1_000_000);

  private long nodeCount = 0;

  private long edgeCount = 0;

  private final Multiset<EdgeLabel> edgeLabelMultiset = TreeMultiset.create(Comparator.comparing(EdgeLabel::name));
  private final Multiset<NodeLabels> nodeLabelsMultiset = TreeMultiset.create(Comparator.comparing(NodeLabels::name));

  @Inject
  public CsvExporter(@Nonnull OntologyDocumentAxiomTranslator axiomTranslator,
                     @Nonnull CsvWriter<Node> nodesCsvWriter,
                     @Nonnull CsvWriter<Edge> relationshipsCsvWriter) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.nodesCsvWriter = checkNotNull(nodesCsvWriter);
    this.relationshipsCsvWriter = checkNotNull(relationshipsCsvWriter);
  }

  public ImmutableMultiset<NodeLabels> getNodeLabelsMultiset() {
    return ImmutableMultiset.copyOf(nodeLabelsMultiset);
  }

  public ImmutableMultiset<EdgeLabel> getEdgeLabelMultiset() {
    return ImmutableMultiset.copyOf(edgeLabelMultiset);
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
    writeNode(translation.getMainNode());
    for(var edge : translation.getEdges()) {
      writeEdge(edge);
    }
    for(var t : translation.getNestedTranslations()) {
      writeTranslation(t);
    }
  }

  private void writeEdge(Edge edge) throws IOException {
    if(writtenNodeEdges.add(EdgeKey.get(edge.getStartId(),
                                        edge.getEndId(),
                                        edge.getLabel()))) {
      edgeCount++;
      relationshipsCsvWriter.write(edge);
      edgeLabelMultiset.add(edge.getLabel());
    }

  }

  private boolean writeNode(Node node) throws IOException {
    if (exportedNodes.add(node.getNodeId().getId())) {
      nodeCount++;
      nodesCsvWriter.write(node);
      nodeLabelsMultiset.add(node.getLabels());
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
}
