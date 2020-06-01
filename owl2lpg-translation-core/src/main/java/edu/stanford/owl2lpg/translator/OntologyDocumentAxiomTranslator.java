package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.*;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROJECT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyDocumentAxiomTranslator {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Inject
  public OntologyDocumentAxiomTranslator(@Nonnull NodeFactory nodeFactory,
                                         @Nonnull AxiomTranslator axiomTranslator) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.axiomTranslator = checkNotNull(axiomTranslator);
  }

  @Nonnull
  public Translation translate(@Nonnull OntologyDocumentId ontologyDocumentId, @Nonnull OWLAxiom axiom) {
    checkNotNull(ontologyDocumentId);
    var axiomTranslation = axiomTranslator.translate(axiom);
    @Nonnull
    var docNode = createOntologyDocumentNode(ontologyDocumentId);
    return Translation.create(ontologyDocumentId, docNode)
                      .connectWith(axiomTranslation, AXIOM, Properties.empty());
  }

  @Nonnull
  private Node createProjectNode(@Nonnull ProjectId projectId) {
    return nodeFactory.createNode(
        projectId,
        PROJECT,
        Properties.of(PROJECT_ID, String.valueOf(projectId.getIdentifier())));
  }

  @Nonnull
  private Node createBranchNode(@Nonnull BranchId branchId) {
    return nodeFactory.createNode(
        branchId,
        NodeLabels.BRANCH,
        Properties.of(BRANCH_ID, String.valueOf(branchId.getIdentifier())));
  }

  @Nonnull
  private Node createOntologyDocumentNode(@Nonnull OntologyDocumentId docId) {
    return nodeFactory.createNode(
        docId,
        NodeLabels.ONTOLOGY_DOCUMENT,
        Properties.of(ONTOLOGY_DOCUMENT_ID, String.valueOf(docId.getIdentifier())));
  }
}
