package edu.stanford.owl2lpg.client.read.ontology.impl;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectAccessorImpl implements ProjectAccessor {

  private static final String ONTOLOGY_IDS_QUERY_FILE = "ontology/ontology-ids.cpy";
  private static final String ONTOLOGY_IDS_QUERY = read(ONTOLOGY_IDS_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Inject
  public ProjectAccessorImpl(@Nonnull Driver driver,
                             @Nonnull EntityAccessor entityAccessor) {
    this.driver = checkNotNull(driver);
    this.entityAccessor = checkNotNull(entityAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OntologyDocumentId> getOntologyDocumentIds(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    return getOntologyDocumentIdMap(projectId, branchId).keySet();
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLOntologyID> getOntologyIds(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    return getOntologyDocumentIdMap(projectId, branchId).values()
        .stream()
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableMap<OntologyDocumentId, OWLOntologyID> getOntologyDocumentIdMap(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    var inputParams = Parameters.forContext(projectId, branchId);
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var outputMap = ImmutableMap.<OntologyDocumentId, OWLOntologyID>builder();
        var result = tx.run(ONTOLOGY_IDS_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next();
          var ontoDocId = OntologyDocumentId.create(row.get("ontoDocId").asString());
          var ontologyIri = Optional.fromNullable(row.get("ontologyIri")).transform(Value::asString).transform(IRI::create);
          var versionIri = Optional.fromNullable(row.get("versionIri")).transform(Value::asString).transform(IRI::create);
          outputMap.put(ontoDocId, new OWLOntologyID(ontologyIri, versionIri));
        }
        return outputMap.build();
      });
    }
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                          @Nonnull BranchId branchId) {
    return getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontoDocId -> entityAccessor.getAllEntities(projectId, branchId, ontoDocId).stream());
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntitiesByIri(@Nonnull IRI entityIri,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId) {
    return getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontoDocId -> entityAccessor.getEntitiesByIri(entityIri, projectId, branchId, ontoDocId).stream());
  }

  @Nonnull
  @Override
  public <E extends OWLEntity> Stream<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                           @Nonnull ProjectId projectId,
                                                           @Nonnull BranchId branchId) {
    return getOntologyDocumentIds(projectId, branchId)
        .stream()
        .flatMap(ontoDocId -> entityAccessor.getEntitiesByType(entityType, projectId, branchId, ontoDocId).stream());
  }
}
