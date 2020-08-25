package edu.stanford.owl2lpg.client.read.signature.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.signature.OntologySignatureAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
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
public class OntologySignatureAccessorImpl implements OntologySignatureAccessor {

  private static final String ONTOLOGY_SIGNATURE_QUERY_FILE = "signature/ontology-signature.cpy";
  private static final String ONTOLOGY_SIGNATURE_QUERY = read(ONTOLOGY_SIGNATURE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;

  @Inject
  public OntologySignatureAccessorImpl(@Nonnull Driver driver,
                                       @Nonnull EntityNodeMapper entityNodeMapper) {
    this.driver = checkNotNull(driver);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri,
                                               @Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    return getEntities(Parameters.forEntityIri(iri, projectId, branchId, ontoDocId));
  }

  @Override
  public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentId ontoDocId) {
    return getEntitiesInSignature(owlEntity.getIRI(), projectId, branchId, ontoDocId).contains(owlEntity);
  }

  @Nonnull
  private ImmutableSet<OWLEntity> getEntities(Value inputParams) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var entities = ImmutableSet.<OWLEntity>builder();
        var result = tx.run(ONTOLOGY_SIGNATURE_QUERY, inputParams);
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
