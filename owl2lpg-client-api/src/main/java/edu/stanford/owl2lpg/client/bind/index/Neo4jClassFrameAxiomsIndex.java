package edu.stanford.owl2lpg.client.bind.index;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomBySubjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.server.index.ClassFrameAxiomsIndex.AnnotationsTreatment.EXCLUDE_ANNOTATIONS;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jClassFrameAxiomsIndex implements ClassFrameAxiomsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final AxiomBySubjectAccessor axiomBySubjectAccessor;

  @Inject
  public Neo4jClassFrameAxiomsIndex(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId,
                                    @Nonnull AxiomBySubjectAccessor axiomBySubjectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.axiomBySubjectAccessor = checkNotNull(axiomBySubjectAccessor);
  }

  @Override
  public Set<OWLAxiom> getFrameAxioms(OWLClass owlClass, AnnotationsTreatment annotationsTreatment) {
    return axiomBySubjectAccessor.getAxiomsBySubject(owlClass, projectId, branchId, ontoDocId)
        .stream()
        .filter(axiom -> {
          var accepted = true;
          if (annotationsTreatment.equals(EXCLUDE_ANNOTATIONS)) {
            accepted = !(axiom instanceof OWLAnnotationAssertionAxiom);
          }
          return accepted;
        })
        .collect(ImmutableSet.toImmutableSet());
  }
}
