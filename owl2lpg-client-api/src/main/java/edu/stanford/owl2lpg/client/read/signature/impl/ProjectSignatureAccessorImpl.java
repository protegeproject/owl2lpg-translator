package edu.stanford.owl2lpg.client.read.signature.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.signature.ProjectSignatureAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectSignatureAccessorImpl implements ProjectSignatureAccessor {

  private static final String PROJECT_INFO_QUERY_FILE = "signature/project-info.cpy";
  private static final String PROJECT_INFO_QUERY = read(PROJECT_INFO_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Inject
  public ProjectSignatureAccessorImpl(@Nonnull Driver driver,
                                      @Nonnull EntityAccessor entityAccessor) {
    this.driver = checkNotNull(driver);
    this.entityAccessor = checkNotNull(entityAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OntologyDocumentId> getOntologyDocumentIds(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    return getProjectInfo("ontoDocId", projectId, branchId)
        .map(OntologyDocumentId::create)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<IRI> getOntologyIris(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    return getProjectInfo("ontologyIri", projectId, branchId)
        .map(IRI::create)
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public ImmutableSet<IRI> getVersionIris(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    return getProjectInfo("versionIri", projectId, branchId)
        .map(IRI::create)
        .collect(ImmutableSet.toImmutableSet());
  }

  private Stream<String> getProjectInfo(@Nonnull String variable,
                                        @Nonnull ProjectId projectId,
                                        @Nonnull BranchId branchId) {
    var inputParams = Parameters.forContext(projectId, branchId);
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var outStream = Stream.<String>builder();
        var result = tx.run(PROJECT_INFO_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          for (var column : row.entrySet()) {
            if (column.getKey().equals(variable)) {
              var value = column.getValue();
              if (value != null) {
                outStream.add((String) value);
              }
            }
          }
        }
        return outStream.build();
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

  @Nonnull
  @Override
  public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId) {
    return getEntitiesByIri(owlEntity.getIRI(), projectId, branchId).anyMatch(owlEntity::equals);
  }
}
