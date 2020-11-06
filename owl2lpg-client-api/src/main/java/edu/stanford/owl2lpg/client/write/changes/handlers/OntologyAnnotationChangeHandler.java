package edu.stanford.owl2lpg.client.write.changes.handlers;

import edu.stanford.bmir.protege.web.server.change.AddOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.server.change.RemoveOntologyAnnotationChange;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface OntologyAnnotationChangeHandler {

  void handle(@Nonnull AddOntologyAnnotationChange addOntologyAnnotationChange);

  void handle(@Nonnull RemoveOntologyAnnotationChange removeOntologyAnnotationChange);
}
