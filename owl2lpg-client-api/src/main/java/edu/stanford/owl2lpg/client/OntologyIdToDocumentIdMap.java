package edu.stanford.owl2lpg.client;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@ProjectSingleton
public class OntologyIdToDocumentIdMap {

  @Nonnull
  private final Driver driver;

  private Map<Key, OntologyDocumentId> documentIdMap = Maps.newHashMap();

  private Set<ProjectId> projectIds = Sets.newHashSet();

  @Inject
  public OntologyIdToDocumentIdMap(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Nonnull
  public synchronized OntologyDocumentId get(@Nonnull ProjectId projectId,
                                             @Nonnull OWLOntologyID ontologyID) {
    ensureLoaded(projectId);
    var key = Key.get(projectId, ontologyID);
    var documentId = documentIdMap.get(key);
    if (documentId == null) {
      documentId = OntologyDocumentId.create();
      documentIdMap.put(key, documentId);
    }
    return documentId;
  }

  private void ensureLoaded(ProjectId projectId) {
    if (projectIds.contains(projectId)) {
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
          documentIdMap.put(Key.get(projectId, ontologyId), documentId);
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

  @AutoValue
  public abstract static class Key {

    @Nonnull
    public static Key get(ProjectId projectId, OWLOntologyID ontologyId) {
      return new AutoValue_OntologyIdToDocumentIdMap_Key(projectId, ontologyId);
    }

    @Nonnull
    public abstract ProjectId getProjectId();

    @Nonnull
    public abstract OWLOntologyID getOntologyId();
  }
}
