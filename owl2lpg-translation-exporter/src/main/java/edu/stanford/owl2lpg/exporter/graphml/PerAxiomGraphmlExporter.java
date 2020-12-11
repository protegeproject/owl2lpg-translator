package edu.stanford.owl2lpg.exporter.graphml;

import edu.stanford.owl2lpg.exporter.common.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.graphml.writer.Neo4jGraphmlWriter;
import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PerAxiomGraphmlExporter {

  @Nonnull
  private final ProjectTranslator projectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

  @Nonnull
  private final Neo4jGraphmlWriter graphmlWriter;

  private Node documentNode = Node.create(NodeId.create(UUID.randomUUID().toString()), ONTOLOGY_DOCUMENT);

  @Inject
  public PerAxiomGraphmlExporter(@Nonnull ProjectTranslator projectTranslator,
                                 @Nonnull AxiomTranslator axiomTranslator,
                                 @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                                 @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                                 @Nonnull Neo4jGraphmlWriter graphmlWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.graphmlWriter = checkNotNull(graphmlWriter);
  }

  public void export(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId) throws IOException {
    var projectTranslation = projectTranslator.translate(projectId, branchId, documentId);
    writeTranslation(projectTranslation);
    documentNode = projectTranslation.nodes(ONTOLOGY_DOCUMENT).findFirst().get();
  }

  private void writeTranslation(Translation translation) {
    graphmlWriter.writeTranslation(translation);
  }

  public void export(@Nonnull OWLAxiom axiom) throws IOException {
    var axiomTranslation = axiomTranslator.translate(axiom);
    writeTranslation(axiomTranslation);
    writeAxiomEdge(axiomTranslation);
    writeInOntologySignatureEdge(axiomTranslation);
  }

  private void writeAxiomEdge(Translation axiomTranslation) {
    var axiomNode = axiomTranslation.getMainNode();
    var axiomEdge = structuralEdgeFactory.getAxiomEdge(documentNode, axiomNode);
    graphmlWriter.writeEdge(axiomEdge);
  }

  private void writeInOntologySignatureEdge(Translation axiomTranslation) {
    var entityNodes = axiomTranslation.nodes(ENTITY);
    entityNodes.forEach(entityNode ->
        augmentedEdgeFactory.getInOntologySignatureEdge(entityNode, documentNode)
            .ifPresent(graphmlWriter::writeEdge));
  }

  public Neo4jGraphmlWriter getGraphmlWriter() {
    return graphmlWriter;
  }

  public void printReport() {
    graphmlWriter.printReport();
  }
}
