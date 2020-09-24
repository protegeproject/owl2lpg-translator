package edu.stanford.owl2lpg.client.bind.change;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.bmir.protege.web.server.change.AddImportChange;
import edu.stanford.bmir.protege.web.server.change.AddOntologyAnnotationChange;
import edu.stanford.bmir.protege.web.server.change.OntologyChangeVisitor;
import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;
import edu.stanford.bmir.protege.web.server.change.RemoveImportChange;
import edu.stanford.bmir.protege.web.server.change.RemoveOntologyAnnotationChange;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jChangeVisitor implements OntologyChangeVisitor {

  @Nonnull
  private final Neo4jAddAxiomChangeVisitor addAxiomChangeVisitor;

  @Nonnull
  private final Neo4jRemoveAxiomChangeVisitor removeAxiomChangeVisitor;

  @Inject
  public Neo4jChangeVisitor(@Nonnull Neo4jAddAxiomChangeVisitor addAxiomChangeVisitor,
                            @Nonnull Neo4jRemoveAxiomChangeVisitor removeAxiomChangeVisitor) {
    this.addAxiomChangeVisitor = checkNotNull(addAxiomChangeVisitor);
    this.removeAxiomChangeVisitor = checkNotNull(removeAxiomChangeVisitor);
  }

  public void visit(@Nonnull AddAxiomChange addAxiomChange) {
    addAxiomChangeVisitor.visit(addAxiomChange);
  }

  public void visit(@Nonnull RemoveAxiomChange removeAxiomChange) {
    removeAxiomChangeVisitor.visit(removeAxiomChange);
  }

  public void visit(@Nonnull AddOntologyAnnotationChange addOntologyAnnotationChange) {
  }

  public void visit(@Nonnull RemoveOntologyAnnotationChange removeOntologyAnnotationChange) {
  }

  public void visit(@Nonnull AddImportChange addImportChange) {
  }

  public void visit(@Nonnull RemoveImportChange removeImportChange) {
  }
}
