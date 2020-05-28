package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
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
  private NodeIdMapper nodeIdMapper;

  @Nonnull
  private final Set<Long> exportedNodes = new HashSet<>();

  private long nodeCount = 0;

  private long edgeCount = 0;

  @Inject
  public CsvExporter(@Nonnull OntologyDocumentAxiomTranslator axiomTranslator,
                     @Nonnull CsvWriter<Node> nodesCsvWriter,
                     @Nonnull CsvWriter<Edge> relationshipsCsvWriter) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.nodesCsvWriter = checkNotNull(nodesCsvWriter);
    this.relationshipsCsvWriter = checkNotNull(relationshipsCsvWriter);
  }

  public void write(@Nonnull OntologyDocumentId documentId,
                    @Nonnull OWLAxiom axiom) throws IOException {
    var translation = axiomTranslator.translate(documentId, axiom);
    writeTranslation(translation);
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
      edgeCount++;
      relationshipsCsvWriter.write(edge);
  }

  private void writeNode(Node node) throws IOException {
    if (exportedNodes.add(node.getNodeId().getId())) {
      nodeCount++;
      nodesCsvWriter.write(node);
    }
  }

  public long getNodeCount() {
    return nodeCount;
  }

  public long getEdgeCount() {
    return edgeCount;
  }
}
