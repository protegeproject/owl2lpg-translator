package edu.stanford.owl2lpg.client.write.handlers.impl;

import edu.stanford.bmir.protege.web.server.change.AddOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.change.RemoveOntologyAnnotationChange;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.write.handlers.OntologyAnnotationChangeHandler;
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
public class OntologyAnnotationChangeHandlerImpl implements OntologyAnnotationChangeHandler {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final AddOntologyAnnotationHandler addOntologyAnnotationHandler;

  @Nonnull
  private final RemoveOntologyAnnotationHandler removeOntologyAnnotationHandler;

  @Inject
  public OntologyAnnotationChangeHandlerImpl(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull DocumentIdMap documentIdMap,
                                             @Nonnull AddOntologyAnnotationHandler addOntologyAnnotationHandler,
                                             @Nonnull RemoveOntologyAnnotationHandler removeOntologyAnnotationHandler) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.addOntologyAnnotationHandler = checkNotNull(addOntologyAnnotationHandler);
    this.removeOntologyAnnotationHandler = checkNotNull(removeOntologyAnnotationHandler);
  }

  @Override
  public void handle(@Nonnull AddOntologyAnnotationChange addOntologyAnnotationChange) {
    var documentId = getOntDocIdFromChange(addOntologyAnnotationChange);
    var annotation = addOntologyAnnotationChange.getAnnotation();
    addOntologyAnnotationHandler.handle(projectId, branchId, documentId, annotation);
  }

  @Override
  public void handle(@Nonnull RemoveOntologyAnnotationChange removeOntologyAnnotationChange) {
    var documentId = getOntDocIdFromChange(removeOntologyAnnotationChange);
    var annotation = removeOntologyAnnotationChange.getAnnotation();
    removeOntologyAnnotationHandler.handle(projectId, branchId, documentId, annotation);
  }

  @Nonnull
  private OntologyDocumentId getOntDocIdFromChange(@Nonnull OntologyChange ontologyChange) {
    var ontologyId = ontologyChange.getOntologyId();
    return documentIdMap.get(projectId, ontologyId);
  }
}
