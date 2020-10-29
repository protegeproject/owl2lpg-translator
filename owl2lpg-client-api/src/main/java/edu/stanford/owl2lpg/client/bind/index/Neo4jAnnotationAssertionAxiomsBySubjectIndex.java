package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.AnnotationAssertionAxiomsBySubjectIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jAnnotationAssertionAxiomsBySubjectIndex implements AnnotationAssertionAxiomsBySubjectIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final AssertionAxiomAccessor assertionAxiomAccessor;

  @Inject
  public Neo4jAnnotationAssertionAxiomsBySubjectIndex(@Nonnull ProjectId projectId,
                                                      @Nonnull BranchId branchId,
                                                      @Nonnull AssertionAxiomAccessor assertionAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.assertionAxiomAccessor = checkNotNull(assertionAxiomAccessor);
  }

  @Override
  public Stream<OWLAnnotationAssertionAxiom> getAxiomsForSubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                                                 @Nonnull OntologyDocumentId ontDocId) {
    return assertionAxiomAccessor
        .getAnnotationAssertionsBySubject(owlAnnotationSubject, projectId, branchId, ontDocId)
        .stream();
  }
}
