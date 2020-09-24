package edu.stanford.owl2lpg.client.bind.change;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.owl2lpg.client.write.handlers.AnnotationAssertionAxiomChangeHandler;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.semanticweb.owlapi.model.AxiomType.ANNOTATION_ASSERTION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAddAxiomChangeVisitor {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AnnotationAssertionAxiomChangeHandler annotationAssertionAxiomChangeHandler;

  @Inject
  public Neo4jAddAxiomChangeVisitor(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId,
                                    @Nonnull AnnotationAssertionAxiomChangeHandler annotationAssertionAxiomChangeHandler) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.annotationAssertionAxiomChangeHandler = checkNotNull(annotationAssertionAxiomChangeHandler);
  }

  public void visit(@Nonnull AddAxiomChange addAxiomChange) {
    var axiom = addAxiomChange.getAxiom();
    if (axiom.isOfType(ANNOTATION_ASSERTION)) {
      annotationAssertionAxiomChangeHandler.handleAdd(projectId, branchId, ontoDocId, (OWLAnnotationAssertionAxiom) axiom);
    }
  }
}
