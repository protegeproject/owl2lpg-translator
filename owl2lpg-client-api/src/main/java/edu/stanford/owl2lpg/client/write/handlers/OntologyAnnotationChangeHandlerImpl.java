package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.bmir.protege.web.server.change.AddOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.server.change.RemoveOntologyAnnotationChange;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyAnnotationChangeHandlerImpl implements OntologyAnnotationChangeHandler {

  @Nonnull
  private final AddOntologyAnnotationHandler addOntologyAnnotationHandler;

  @Nonnull
  private final RemoveOntologyAnnotationHandler removeOntologyAnnotationHandler;

  @Inject
  public OntologyAnnotationChangeHandlerImpl(@Nonnull AddOntologyAnnotationHandler addOntologyAnnotationHandler,
                                             @Nonnull RemoveOntologyAnnotationHandler removeOntologyAnnotationHandler) {
    this.addOntologyAnnotationHandler = checkNotNull(addOntologyAnnotationHandler);
    this.removeOntologyAnnotationHandler = checkNotNull(removeOntologyAnnotationHandler);
  }

  @Override
  public void handle(@Nonnull AddOntologyAnnotationChange addOntologyAnnotationChange) {
    var ontologyId = addOntologyAnnotationChange.getOntologyId();
    var annotation = addOntologyAnnotationChange.getAnnotation();
    addOntologyAnnotationHandler.handle(ontologyId, annotation);
  }

  @Override
  public void handle(@Nonnull RemoveOntologyAnnotationChange removeOntologyAnnotationChange) {
    var ontologyId = removeOntologyAnnotationChange.getOntologyId();
    var annotation = removeOntologyAnnotationChange.getAnnotation();
    removeOntologyAnnotationHandler.handle(ontologyId, annotation);
  }
}
