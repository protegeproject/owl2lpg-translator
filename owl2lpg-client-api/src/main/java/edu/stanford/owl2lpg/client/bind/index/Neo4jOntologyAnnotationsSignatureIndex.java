package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAnnotationsSignatureIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jOntologyAnnotationsSignatureIndex implements OntologyAnnotationsSignatureIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyAnnotationsAccessor ontologyAnnotationsAccessor;

  @Inject
  public Neo4jOntologyAnnotationsSignatureIndex(@Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull OntologyAnnotationsAccessor ontologyAnnotationsAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontologyAnnotationsAccessor = checkNotNull(ontologyAnnotationsAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationProperty> getOntologyAnnotationsSignature(@Nonnull OntologyDocumentId ontDocId) {
    return ontologyAnnotationsAccessor.getOntologyAnnotationProperties(projectId, branchId, ontDocId).stream();
  }

  @Override
  public boolean containsEntityInOntologyAnnotationsSignature(@Nonnull OWLEntity owlEntity,
                                                              @Nonnull OntologyDocumentId ontDocId) {
    return ontologyAnnotationsAccessor.getOntologyAnnotations(projectId, branchId, ontDocId)
        .stream()
        .map(OWLAnnotation::getSignature)
        .anyMatch(annotationSignature -> annotationSignature.contains(owlEntity));
  }
}
