package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.OntologyAxiomsSignatureIndex;
import edu.stanford.owl2lpg.client.read.ontology.OntologyAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

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
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final OntologyAccessor ontologyAccessor;

  @Inject
  public Neo4jOntologyAxiomsSignatureIndex(@Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentId ontoDocId,
                                           @Nonnull OntologyAccessor ontologyAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.ontologyAccessor = checkNotNull(ontologyAccessor);
  }

  @Override
  public <E extends OWLEntity> Stream<E> getOntologyAxiomsSignature(@Nonnull EntityType<E> entityType,
                                                                    @Nonnull OWLOntologyID owlOntologyID) {
    return ontologyAccessor.getEntitiesByType(entityType, projectId, branchId, ontoDocId).stream();
  }

  @Override
  public boolean containsEntityInOntologyAxiomsSignature(@Nonnull OWLEntity owlEntity,
                                                         @Nonnull OWLOntologyID owlOntologyID) {
    return ontologyAccessor.getEntitiesByIri(owlEntity.getIRI(), projectId, branchId, ontoDocId)
        .stream()
        .anyMatch(owlEntity::equals);
  }
}
