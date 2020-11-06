package edu.stanford.owl2lpg.client.read.ontology.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IS_DEFAULT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectAccessorImpl implements ProjectAccessor {

  private static final String BRANCH_IDS_QUERY_FILE = "read/ontology/branch-ids.cpy";
  private static final String ONTOLOGY_DOCUMENT_IDS_QUERY_FILE = "read/ontology/ontology-document-ids.cpy";
  private static final String ONTOLOGY_IDS_QUERY_FILE = "read/ontology/ontology-ids.cpy";

  private static final String BRANCH_IDS_QUERY = read(BRANCH_IDS_QUERY_FILE);
  private static final String ONTOLOGY_DOCUMENT_IDS_QUERY = read(ONTOLOGY_DOCUMENT_IDS_QUERY_FILE);
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
  public Optional<BranchId> getDefaultBranchId(@Nonnull ProjectId projectId) {
    return getBranchIds(projectId)
        .stream()
        .filter(BranchId::isDefault)
        .findFirst();
  }

  @Nonnull
  @Override
  public ImmutableSet<BranchId> getBranchIds(@Nonnull ProjectId projectId) {
    var inputParams = Parameters.forContext(projectId);
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var branchIds = ImmutableSet.<BranchId>builder();
        var result = tx.run(BRANCH_IDS_QUERY, inputParams);
        while (result.hasNext()) {
          var record = result.next();
          var node = record.get("n").asNode();
          var branchId = node.get(BRANCH_ID).asString();
          var isDefault = node.get(IS_DEFAULT).asBoolean();
          branchIds.add(BranchId.get(branchId, isDefault));
        }
        return branchIds.build();
      });
    }
  }

  @Nonnull
  @Override
  public Optional<OntologyDocumentId> getDefaultOntologyDocumentId(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    return getOntologyDocumentIds(projectId, branchId)
        .stream()
        .filter(OntologyDocumentId::isDefault)
        .findFirst();
  }

  @Nonnull
  @Override
  public ImmutableSet<OntologyDocumentId> getOntologyDocumentIds(@Nonnull ProjectId projectId, @Nonnull BranchId branchId) {
    var inputParams = Parameters.forContext(projectId, branchId);
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var ontDocIds = ImmutableSet.<OntologyDocumentId>builder();
        var result = tx.run(ONTOLOGY_DOCUMENT_IDS_QUERY, inputParams);
        while (result.hasNext()) {
          var record = result.next();
          var node = record.get("n").asNode();
          var ontDocId = node.get(ONTOLOGY_DOCUMENT_ID).asString();
          var isDefault = node.get(IS_DEFAULT).asBoolean();
          ontDocIds.add(OntologyDocumentId.get(ontDocId, isDefault));
        }
        return ontDocIds.build();
      });
    }
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
          var ontoDocId = OntologyDocumentId.get(row.get("ontoDocId").asString());
          var ontologyIri = com.google.common.base.Optional.fromNullable(row.get("ontologyIri"))
              .transform(Value::asString)
              .transform(IRI::create);
          var versionIri = com.google.common.base.Optional.fromNullable(row.get("versionIri"))
              .transform(Value::asString)
              .transform(IRI::create);
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
