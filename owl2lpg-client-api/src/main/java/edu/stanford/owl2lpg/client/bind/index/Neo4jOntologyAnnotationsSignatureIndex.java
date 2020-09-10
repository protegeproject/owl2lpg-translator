package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAnnotationsSignatureIndex;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
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
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final OntologyAnnotationsAccessor ontologyAnnotationsAccessor;

  public Neo4jOntologyAnnotationsSignatureIndex(@Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull OntologyDocumentId ontoDocId,
                                                @Nonnull OntologyAnnotationsAccessor ontologyAnnotationsAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.ontologyAnnotationsAccessor = checkNotNull(ontologyAnnotationsAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationProperty> getOntologyAnnotationsSignature(@Nonnull OWLOntologyID owlOntologyID) {
    return ontologyAnnotationsAccessor.getOntologyAnnotationProperties(projectId, branchId, ontoDocId).stream();
  }

  @Override
  public boolean containsEntityInOntologyAnnotationsSignature(@Nonnull OWLEntity owlEntity,
                                                              @Nonnull OWLOntologyID owlOntologyID) {
    return ontologyAnnotationsAccessor.getOntologyAnnotations(projectId, branchId, ontoDocId)
        .stream()
        .map(OWLAnnotation::getSignature)
        .anyMatch(annotationSignature -> annotationSignature.contains(owlEntity));
  }
}
