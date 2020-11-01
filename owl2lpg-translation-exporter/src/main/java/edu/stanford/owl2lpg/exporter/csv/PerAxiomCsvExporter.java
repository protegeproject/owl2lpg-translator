package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.exporter.csv.internal.ProjectTranslator;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvOutputWriter;
import edu.stanford.owl2lpg.model.AugmentedEdgeFactory;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
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
  private final CsvOutputWriter csvOutputWriter;

  private Node documentNode = Node.create(NodeId.create(UUID.randomUUID().toString()), ONTOLOGY_DOCUMENT);

  @Inject
  public PerAxiomCsvExporter(@Nonnull ProjectTranslator projectTranslator,
                             @Nonnull AxiomTranslator axiomTranslator,
                             @Nonnull StructuralEdgeFactory structuralEdgeFactory,
                             @Nonnull AugmentedEdgeFactory augmentedEdgeFactory,
                             @Nonnull CsvOutputWriter csvOutputWriter) {
    this.projectTranslator = checkNotNull(projectTranslator);
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.structuralEdgeFactory = checkNotNull(structuralEdgeFactory);
    this.augmentedEdgeFactory = checkNotNull(augmentedEdgeFactory);
    this.csvOutputWriter = checkNotNull(csvOutputWriter);
  }

  public void export(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId) {
    var projectTranslation = projectTranslator.translate(projectId, branchId, documentId);
    writeTranslation(projectTranslation);
    projectTranslation.nodes(ONTOLOGY_DOCUMENT).findFirst()
        .ifPresent(documentNode -> this.documentNode = documentNode);
  }

  private void writeTranslation(Translation translation) {
    csvOutputWriter.write(translation);
  }

  public void export(@Nonnull OWLAxiom axiom) {
    var axiomTranslation = axiomTranslator.translate(axiom);
    writeTranslation(axiomTranslation);
    writeAxiomEdge(axiomTranslation);
    writeInOntologySignatureEdge(axiomTranslation);
  }

  private void writeAxiomEdge(Translation axiomTranslation) {
    var axiomNode = axiomTranslation.getMainNode();
    var axiomEdge = structuralEdgeFactory.getAxiomEdge(documentNode, axiomNode);
    csvOutputWriter.write(axiomEdge);
  }

  private void writeInOntologySignatureEdge(Translation axiomTranslation) {
    var entityNodes = axiomTranslation.nodes(ENTITY);
    entityNodes.forEach(entityNode ->
        augmentedEdgeFactory.getInOntologySignatureEdge(entityNode, documentNode)
            .ifPresent(csvOutputWriter::write));
  }

  public CsvOutputWriter getWriter() {
    return csvOutputWriter;
  }

  public void printReport() {
    csvOutputWriter.printReport();
  }
}
