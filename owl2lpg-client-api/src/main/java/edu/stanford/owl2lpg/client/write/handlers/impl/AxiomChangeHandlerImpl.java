package edu.stanford.owl2lpg.client.write.handlers.impl;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.bmir.protege.web.server.change.AxiomChange;
import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.write.handlers.AxiomChangeHandler;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomChangeHandlerImpl implements AxiomChangeHandler {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AddAxiomHandler addAxiomHandler;

  @Nonnull
  private final RemoveAxiomHandler removeAxiomHandler;

  @Inject
  public AxiomChangeHandlerImpl(@Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull DocumentIdMap documentIdMap,
                                @Nonnull AddAxiomHandler addAxiomHandler,
                                @Nonnull RemoveAxiomHandler removeAxiomHandler) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.addAxiomHandler = checkNotNull(addAxiomHandler);
    this.removeAxiomHandler = checkNotNull(removeAxiomHandler);
  }

  @Override
  public void handle(@Nonnull AddAxiomChange addAxiomChange) {
    var documentId = getOntDocIdFromChange(addAxiomChange);
    var axiom = addAxiomChange.getAxiom();
    addAxiomHandler.handle(projectId, branchId, documentId, axiom);
  }

  @Override
  public void handle(@Nonnull RemoveAxiomChange removeAxiomChange) {
    var documentId = getOntDocIdFromChange(removeAxiomChange);
    var axiom = removeAxiomChange.getAxiom();
    removeAxiomHandler.handle(projectId, branchId, documentId, axiom);
  }

  @Nonnull
  private OntologyDocumentId getOntDocIdFromChange(@Nonnull AxiomChange axiomChange) {
    var ontologyId = axiomChange.getOntologyId();
    return documentIdMap.get(projectId, ontologyId);
  }
}
