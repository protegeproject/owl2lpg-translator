package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAxiomsSignatureIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.ontology.OntologyDocumentAccessor;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jOntologyAxiomsSignatureIndex implements OntologyAxiomsSignatureIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentAccessor ontologyDocumentAccessor;

  @Inject
  public Neo4jOntologyAxiomsSignatureIndex(@Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentAccessor ontologyDocumentAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontologyDocumentAccessor = checkNotNull(ontologyDocumentAccessor);
  }

  @Override
  public <E extends OWLEntity> Stream<E> getOntologyAxiomsSignature(@Nonnull EntityType<E> entityType,
                                                                    @Nonnull OntologyDocumentId ontDocId) {
    return ontologyDocumentAccessor.getEntitiesByType(entityType, projectId, branchId, ontDocId).stream();
  }

  @Override
  public boolean containsEntityInOntologyAxiomsSignature(@Nonnull OWLEntity owlEntity,
                                                         @Nonnull OntologyDocumentId ontDocId) {
    return ontologyDocumentAccessor.getEntitiesByIri(owlEntity.getIRI(), projectId, branchId, ontDocId)
        .stream()
        .anyMatch(owlEntity::equals);
  }
}
