package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.exporter.csv.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.csv.writer.Neo4jCsvWriter;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactory;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
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
public class PerAxiomCsvExporter {

  @Nonnull
  private final ProjectTranslator projectTranslator;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final StructuralEdgeFactory structuralEdgeFactory;

  @Nonnull
  private final AugmentedEdgeFactory augmentedEdgeFactory;

  @Nonnull
  private final Neo4jCsvWriter csvWriter;

  private Node documentNode = Node.create(NodeId.create(UUID.randomUUID().toString()), ONTOLOGY_DOCUMENT);

  @Inject
  public PerAxiomCsvExporter(@Nonnull ProjectTranslator projectTranslator,
                             @Nonnull AxiomTranslator axiomTranslator,
                             @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                             @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                             @Nonnull Neo4jCsvWriter csvWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.csvWriter = checkNotNull(csvWriter);
  }

  public void export(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId) throws IOException {
    var projectTranslation = projectTranslator.translate(projectId, branchId, documentId);
    writeTranslation(projectTranslation);
    documentNode = projectTranslation.nodes(ONTOLOGY_DOCUMENT).findFirst().get();
  }

  private void writeTranslation(Translation translation) {
    csvWriter.writeTranslation(translation);
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
    csvWriter.writeEdge(axiomEdge);
  }

  private void writeInOntologySignatureEdge(Translation axiomTranslation) {
    var entityNodes = axiomTranslation.nodes(ENTITY);
    entityNodes.forEach(entityNode ->
        augmentedEdgeFactory.getInOntologySignatureEdge(entityNode, documentNode)
            .ifPresent(csvWriter::writeEdge));
  }

  public void printReport() {
    csvWriter.printReport();
  }
}
