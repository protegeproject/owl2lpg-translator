package edu.stanford.owl2lpg.client.read.entity.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityAccessorImpl implements EntityAccessor {

  private static final String ALL_ENTITIES_QUERY_FILE = "entities/all-entities.cpy";
  private static final String ENTITIES_BY_IRI_QUERY_FILE = "entities/entities-by-iri.cpy";
  private static final String ENTITIES_BY_TYPE_QUERY_FILE = "entities/entities-by-type.cpy";

  private static final String ALL_ENTITIES_QUERY = read(ALL_ENTITIES_QUERY_FILE);
  private static final String ENTITIES_BY_IRI_QUERY = read(ENTITIES_BY_IRI_QUERY_FILE);
  private static final String ENTITIES_BY_TYPE_QUERY = read(ENTITIES_BY_TYPE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;


  @Inject
  public EntityAccessorImpl(@Nonnull Driver driver,
                            @Nonnull EntityNodeMapper entityNodeMapper) {
    this.driver = checkNotNull(driver);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId) {
    return getEntities(ALL_ENTITIES_QUERY, Parameters.forContext(projectId, branchId, ontoDocId));
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getEntitiesByIri(@Nonnull IRI entityIri,
                                         @Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull OntologyDocumentId ontoDocId) {
    return getEntities(ENTITIES_BY_IRI_QUERY, Parameters.forEntityIri(entityIri, projectId, branchId, ontoDocId));
  }

  @Nonnull
  private ImmutableSet<OWLEntity> getEntities(String queryString, Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var entities = ImmutableSet.<OWLEntity>builder();
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              var owlEntity = entityNodeMapper.toOwlEntity(node);
              entities.add(owlEntity);
            }
          }
        }
        return entities.build();
      });
    }
  }

  @Nonnull
  @Override
  public <E extends OWLEntity> Set<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forEntityType(entityType, projectId, branchId, ontoDocId);
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var entities = ImmutableSet.<E>builder();
        var result = tx.run(ENTITIES_BY_TYPE_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals("n")) {
              var node = (Node) column.getValue();
              var owlEntity = (E) entityNodeMapper.toOwlEntity(node);
              entities.add(owlEntity);
            }
          }
        }
        return entities.build();
      });
    }
  }
}
