package edu.stanford.owl2lpg.client.bind.change;

import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;
import edu.stanford.owl2lpg.client.write.handlers.AnnotationAssertionAxiomChangeHandler;
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
public class Neo4jRemoveAxiomChangeVisitor {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AnnotationAssertionAxiomChangeHandler annotationAssertionAxiomChangeHandler;

  @Inject
  public Neo4jRemoveAxiomChangeVisitor(@Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId,
                                       @Nonnull AnnotationAssertionAxiomChangeHandler annotationAssertionAxiomChangeHandler) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.annotationAssertionAxiomChangeHandler = checkNotNull(annotationAssertionAxiomChangeHandler);
  }

  public void visit(@Nonnull RemoveAxiomChange removeAxiomChange) {
    var axiom = removeAxiomChange.getAxiom();
    if (axiom instanceof OWLAnnotationAssertionAxiom) {
      annotationAssertionAxiomChangeHandler.handleRemove(projectId, branchId, ontoDocId, (OWLAnnotationAssertionAxiom) axiom);
    }
  }
}
