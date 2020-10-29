package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAnnotationsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import org.semanticweb.owlapi.model.OWLAnnotation;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jOntologyAnnotationsIndex implements OntologyAnnotationsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyAnnotationsAccessor ontologyAnnotationsAccessor;

  @Inject
  public Neo4jOntologyAnnotationsIndex(@Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyAnnotationsAccessor ontologyAnnotationsAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontologyAnnotationsAccessor = checkNotNull(ontologyAnnotationsAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotation> getOntologyAnnotations(@Nonnull OntologyDocumentId ontDocId) {
    return ontologyAnnotationsAccessor.getOntologyAnnotations(projectId, branchId, ontDocId).stream();
  }

  @Override
  public boolean containsAnnotation(@Nonnull OWLAnnotation owlAnnotation, @Nonnull OntologyDocumentId ontDocId) {
    return getOntologyAnnotations(ontDocId).anyMatch(owlAnnotation::equals);
  }
}
