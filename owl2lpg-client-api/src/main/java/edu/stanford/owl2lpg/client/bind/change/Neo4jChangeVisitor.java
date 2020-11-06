package edu.stanford.owl2lpg.client.bind.change;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.bmir.protege.web.server.change.AddImportChange;
import edu.stanford.bmir.protege.web.server.change.AddOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeVisitor;
import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;
import edu.stanford.bmir.protege.web.server.change.RemoveImportChange;
import edu.stanford.bmir.protege.web.server.change.RemoveOntologyAnnotationChange;
import edu.stanford.owl2lpg.client.write.changes.handlers.AxiomChangeHandler;
import edu.stanford.owl2lpg.client.write.changes.handlers.OntologyAnnotationChangeHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jChangeVisitor implements OntologyChangeVisitor {

  @Nonnull
  private final AxiomChangeHandler axiomChangeHandler;

  @Nonnull
  private final OntologyAnnotationChangeHandler ontologyAnnotationChangeHandler;

  @Inject
  public Neo4jChangeVisitor(@Nonnull AxiomChangeHandler axiomChangeHandler,
                            @Nonnull OntologyAnnotationChangeHandler ontologyAnnotationChangeHandler) {
    this.axiomChangeHandler = checkNotNull(axiomChangeHandler);
    this.ontologyAnnotationChangeHandler = checkNotNull(ontologyAnnotationChangeHandler);
  }

  public void visit(@Nonnull AddAxiomChange addAxiomChange) {
    axiomChangeHandler.handle(addAxiomChange);
  }

  public void visit(@Nonnull RemoveAxiomChange removeAxiomChange) {
    axiomChangeHandler.handle(removeAxiomChange);
  }

  public void visit(@Nonnull AddOntologyAnnotationChange addOntologyAnnotationChange) {
    ontologyAnnotationChangeHandler.handle(addOntologyAnnotationChange);
  }

  public void visit(@Nonnull RemoveOntologyAnnotationChange removeOntologyAnnotationChange) {
    ontologyAnnotationChangeHandler.handle(removeOntologyAnnotationChange);
  }

  public void visit(@Nonnull AddImportChange addImportChange) {
  }

  public void visit(@Nonnull RemoveImportChange removeImportChange) {
  }
}
