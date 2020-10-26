package edu.stanford.owl2lpg.client;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@ProjectSingleton
public class DocumentIdMap {

  @Nonnull
  private final Driver driver;

  private final Map<ProjectId, Map<OWLOntologyID, OntologyDocumentId>> documentIdMap = Maps.newHashMap();

  @Inject
  public DocumentIdMap(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Nonnull
  public synchronized OntologyDocumentId get(@Nonnull ProjectId projectId,
                                             @Nonnull OWLOntologyID ontologyId) {
    var innerMap = getDocumentIdInnerMap(projectId);
    var documentId = innerMap.get(ontologyId);
    if (documentId == null) {
      documentId = OntologyDocumentId.create();
      innerMap.put(ontologyId, documentId);
    }
    return documentId;
  }

  @Nonnull
  public synchronized ImmutableSet<OntologyDocumentId> get(@Nonnull ProjectId projectId) {
    return ImmutableSet.copyOf(getDocumentIdInnerMap(projectId).values());
  }

  @Nonnull
  private Map<OWLOntologyID, OntologyDocumentId> getDocumentIdInnerMap(@Nonnull ProjectId projectId) {
    ensureLoaded(projectId);
    return documentIdMap.computeIfAbsent(projectId, k -> Maps.newHashMap());
  }

  private void ensureLoaded(ProjectId projectId) {
    if (documentIdMap.containsKey(projectId)) {
      return;
    }
    load(projectId);
  }

  private void load(ProjectId projectId) {
    var queryString =
        "MATCH (:Project {projectId:$projectId})-[:BRANCH]->(:Branch)-[:ONTOLOGY_DOCUMENT]-(o:OntologyDocument)\n" +
            "OPTIONAL MATCH (o)-[:ONTOLOGY_IRI]->(i:IRI)\n" +
            "OPTIONAL MATCH (o)-[:VERSION_IRI]->(v:IRI)\n" +
            "RETURN i.iri AS ontologyIri, v.iri AS versionIri, o.ontologyDocumentId AS documentId";
    var inputParams = new MapValue(Map.of("projectId", new StringValue(projectId.getIdentifier())));
    try (var session = driver.session()) {
      session.readTransaction(tx -> {
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var record = result.next();
          var ontologyIri = getIri(record, "ontologyIri");
          var versionIri = getIri(record, "versionIri");
          var documentId = getDocumentId(record);
          var ontologyId = new OWLOntologyID(ontologyIri, versionIri);
          var innerMap = documentIdMap.computeIfAbsent(projectId, k -> Maps.newHashMap());
          innerMap.put(ontologyId, documentId);
        }
        return 1;
      });
    }
  }

  @Nonnull
  private Optional<IRI> getIri(Record record, String variableName) {
    var value = record.get(variableName);
    return (!value.isNull()) ?
        Optional.of(IRI.create(value.asString())) :
        Optional.absent();
  }

  private OntologyDocumentId getDocumentId(Record record) {
    var documentId = record.get("documentId").asString();
    return OntologyDocumentId.create(documentId);
  }
}
