package edu.stanford.owl2lpg.model;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.BRANCH;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROJECT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyContextNodeFactory {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Inject
  public OntologyContextNodeFactory(@Nonnull NodeFactory nodeFactory) {
    this.nodeFactory = checkNotNull(nodeFactory);
  }

  public Node createProjectNode(@Nonnull ProjectId projectId) {
    return nodeFactory.createNode(projectId, PROJECT, Properties.of(PROJECT_ID, projectId.getIdentifier()));
  }

  public Node createBranchNode(@Nonnull BranchId branchId) {
    return nodeFactory.createNode(branchId, BRANCH, Properties.of(BRANCH_ID, branchId.getIdentifier()));
  }

  public Node createOntologyDocumentNode(@Nonnull OntologyDocumentId ontoDocId) {
    return nodeFactory.createNode(ontoDocId, ONTOLOGY_DOCUMENT, Properties.of(ONTOLOGY_DOCUMENT_ID, ontoDocId.getIdentifier()));
  }
}
