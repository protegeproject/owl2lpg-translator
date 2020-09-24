package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationAssertionAxiomChangeHandler implements AxiomChangeHandler<OWLAnnotationAssertionAxiom> {

  @Nonnull
  private final AddAnnotationAssertionAxiom addAnnotationAssertionAxiom;

  @Nonnull
  private final RemoveAnnotationAssertionAxiom removeAnnotationAssertionAxiom;

  @Inject
  public AnnotationAssertionAxiomChangeHandler(@Nonnull AddAnnotationAssertionAxiom addAnnotationAssertionAxiom,
                                               @Nonnull RemoveAnnotationAssertionAxiom removeAnnotationAssertionAxiom) {
    this.addAnnotationAssertionAxiom = checkNotNull(addAnnotationAssertionAxiom);
    this.removeAnnotationAssertionAxiom = checkNotNull(removeAnnotationAssertionAxiom);
  }

  @Override
  public void handleAdd(@Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId,
                        @Nonnull OWLAnnotationAssertionAxiom annotationAssertion) {
    if (annotationAssertion.getSubject().isAnonymous()) {
      return;
    }
    addAnnotationAssertionAxiom.add(projectId, branchId, ontoDocId, annotationAssertion);
  }

  @Override
  public void handleRemove(@Nonnull ProjectId projectId,
                           @Nonnull BranchId branchId,
                           @Nonnull OntologyDocumentId ontoDocId,
                           @Nonnull OWLAnnotationAssertionAxiom annotationAssertion) {
    if (annotationAssertion.getSubject().isAnonymous()) {
      return;
    }
    removeAnnotationAssertionAxiom.remove(projectId, branchId, ontoDocId, annotationAssertion);
  }
}
