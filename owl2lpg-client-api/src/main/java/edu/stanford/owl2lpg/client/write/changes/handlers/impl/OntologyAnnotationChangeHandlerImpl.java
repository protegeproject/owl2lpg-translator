package edu.stanford.owl2lpg.client.write.changes.handlers.impl;

import edu.stanford.bmir.protege.web.server.change.AddOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.server.change.RemoveOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.owl2lpg.client.write.changes.handlers.OntologyAnnotationChangeHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyAnnotationChangeHandlerImpl implements OntologyAnnotationChangeHandler {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final AddOntologyAnnotationHandler addOntologyAnnotationHandler;

  @Nonnull
  private final RemoveOntologyAnnotationHandler removeOntologyAnnotationHandler;

  @Inject
  public OntologyAnnotationChangeHandlerImpl(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull ProjectAccessor projectAccessor,
                                             @Nonnull AddOntologyAnnotationHandler addOntologyAnnotationHandler,
                                             @Nonnull RemoveOntologyAnnotationHandler removeOntologyAnnotationHandler) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.addOntologyAnnotationHandler = checkNotNull(addOntologyAnnotationHandler);
    this.removeOntologyAnnotationHandler = checkNotNull(removeOntologyAnnotationHandler);
  }

  @Override
  public void handle(@Nonnull AddOntologyAnnotationChange addOntologyAnnotationChange) {
    var ontDocId = addOntologyAnnotationChange.getOntologyDocumentId();
    var annotation = addOntologyAnnotationChange.getAnnotation();
    addOntologyAnnotationHandler.handle(projectId, branchId, ontDocId, annotation);
  }

  @Override
  public void handle(@Nonnull RemoveOntologyAnnotationChange removeOntologyAnnotationChange) {
    var ontDocId = removeOntologyAnnotationChange.getOntologyDocumentId();
    var annotation = removeOntologyAnnotationChange.getAnnotation();
    removeOntologyAnnotationHandler.handle(projectId, branchId, ontDocId, annotation);
  }
}
