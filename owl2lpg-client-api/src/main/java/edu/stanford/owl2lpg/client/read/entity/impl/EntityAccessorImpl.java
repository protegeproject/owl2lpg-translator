package edu.stanford.owl2lpg.client.read.entity.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.GraphReader;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityAccessorImpl implements EntityAccessor {

  private static final String ALL_ENTITIES_QUERY_FILE = "read/entities/all-entities.cpy";
  private static final String ENTITIES_BY_IRI_QUERY_FILE = "read/entities/entities-by-iri.cpy";
  private static final String ENTITIES_BY_TYPE_QUERY_FILE = "read/entities/entities-by-type.cpy";

  private static final String ALL_ENTITIES_QUERY = read(ALL_ENTITIES_QUERY_FILE);
  private static final String ENTITIES_BY_IRI_QUERY = read(ENTITIES_BY_IRI_QUERY_FILE);
  private static final String ENTITIES_BY_TYPE_QUERY = read(ENTITIES_BY_TYPE_QUERY_FILE);

  @Nonnull
  private final GraphReader graphReader;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;
  
  @Inject
  public EntityAccessorImpl(@Nonnull GraphReader graphReader,
                            @Nonnull EntityNodeMapper entityNodeMapper) {
    this.graphReader = checkNotNull(graphReader);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull OntologyDocumentId ontoDocId) {
    return graphReader.getNodes(ALL_ENTITIES_QUERY, Parameters.forContext(projectId, branchId, ontoDocId))
        .stream()
        .map(entityNodeMapper::toOwlEntity)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntity> getEntitiesByIri(@Nonnull IRI entityIri,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId) {
    return graphReader.getNodes(ENTITIES_BY_IRI_QUERY, Parameters.forEntityIri(entityIri, projectId, branchId, ontoDocId))
        .stream()
        .map(entityNodeMapper::toOwlEntity)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public <E extends OWLEntity> ImmutableSet<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId,
                                                                 @Nonnull OntologyDocumentId ontoDocId) {
    return graphReader.getNodes(ENTITIES_BY_TYPE_QUERY, Parameters.forEntityType(entityType, projectId, branchId, ontoDocId))
        .stream()
        .map(node -> (E) entityNodeMapper.toOwlEntity(node))
        .collect(ImmutableSet.toImmutableSet());
  }
}
