package edu.stanford.owl2lpg.client.read.signature.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityNodeMapper;
import edu.stanford.owl2lpg.client.read.signature.ProjectSignatureAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
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
public class ProjectSignatureAccessorImpl implements ProjectSignatureAccessor {

  private static final String PROJECT_SIGNATURE_QUERY_FILE = "signature/project-signature.cpy";
  private static final String PROJECT_SIGNATURE_QUERY = read(PROJECT_SIGNATURE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;

  @Inject
  public ProjectSignatureAccessorImpl(@Nonnull Driver driver,
                                      @Nonnull EntityNodeMapper entityNodeMapper) {
    this.driver = checkNotNull(driver);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId) {
    return getEntities(Parameters.forEntityIri(iri, projectId, branchId));
  }

  @Nonnull
  @Override
  public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId) {
    return getEntitiesInSignature(owlEntity.getIRI(), projectId, branchId).contains(owlEntity);
  }

  @Nonnull
  private ImmutableSet<OWLEntity> getEntities(Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var entities = ImmutableSet.<OWLEntity>builder();
        var result = tx.run(PROJECT_SIGNATURE_QUERY, inputParams);
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
}
