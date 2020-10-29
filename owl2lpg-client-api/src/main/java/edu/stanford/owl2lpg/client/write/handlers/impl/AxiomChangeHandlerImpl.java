package edu.stanford.owl2lpg.client.write.handlers.impl;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.write.handlers.AxiomChangeHandler;

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
  private final AddAxiomHandler addAxiomHandler;

  @Nonnull
  private final RemoveAxiomHandler removeAxiomHandler;

  @Inject
  public AxiomChangeHandlerImpl(@Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull AddAxiomHandler addAxiomHandler,
                                @Nonnull RemoveAxiomHandler removeAxiomHandler) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.addAxiomHandler = checkNotNull(addAxiomHandler);
    this.removeAxiomHandler = checkNotNull(removeAxiomHandler);
  }

  @Override
  public void handle(@Nonnull AddAxiomChange addAxiomChange) {
    var ontDocId = addAxiomChange.getOntologyDocumentId();
    var axiom = addAxiomChange.getAxiom();
    addAxiomHandler.handle(projectId, branchId, ontDocId, axiom);
  }

  @Override
  public void handle(@Nonnull RemoveAxiomChange removeAxiomChange) {
    var ontDocId = removeAxiomChange.getOntologyDocumentId();
    var axiom = removeAxiomChange.getAxiom();
    removeAxiomHandler.handle(projectId, branchId, ontDocId, axiom);
  }
}
