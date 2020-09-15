package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ProjectAnnotationAssertionAxiomsBySubjectIndex;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomBySubjectAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
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
public class Neo4jProjectAnnotationAssertionAxiomsBySubjectIndex implements ProjectAnnotationAssertionAxiomsBySubjectIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor;

  @Inject
  public Neo4jProjectAnnotationAssertionAxiomsBySubjectIndex(@Nonnull ProjectId projectId,
                                                             @Nonnull BranchId branchId,
                                                             @Nonnull ProjectAccessor projectAccessor,
                                                             @Nonnull AssertionAxiomBySubjectAccessor assertionAxiomBySubjectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
    this.assertionAxiomBySubjectAccessor = checkNotNull(assertionAxiomBySubjectAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(@Nonnull OWLAnnotationSubject owlAnnotationSubject) {
    return projectAccessor.getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontoDocId -> assertionAxiomBySubjectAccessor
            .getAnnotationAssertionsBySubject(owlAnnotationSubject, projectId, branchId, ontoDocId)
            .stream());
  }
}
