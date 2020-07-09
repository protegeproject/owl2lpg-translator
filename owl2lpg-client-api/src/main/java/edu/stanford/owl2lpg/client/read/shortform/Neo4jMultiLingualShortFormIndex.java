package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jMultiLingualShortFormIndex implements MultiLingualShortFormIndex {

  private static final String SHORT_FORMS_INDEX_QUERY_FILE = "shortforms/short-forms-index.cpy";
  private static final String SHORT_FORMS_INDEX_QUERY = read(SHORT_FORMS_INDEX_QUERY_FILE);

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Session session;

  @Nonnull
  private final Neo4jNodeTranslator nodeTranslator;

  @Inject
  public Neo4jMultiLingualShortFormIndex(@Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull Session session,
                                         @Nonnull Neo4jNodeTranslator nodeTranslator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.session = checkNotNull(session);
    this.nodeTranslator = checkNotNull(nodeTranslator);
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntities(@Nonnull String entityName,
                                       @Nonnull List<DictionaryLanguage> languages) {
    var entityMap = getEntities(entityName);
    return languages
        .stream()
        .map(language -> entityMap.getOrDefault(language, null))
        .filter(Objects::nonNull)
        .distinct();
  }

  @Nonnull
  public ImmutableMap<DictionaryLanguage, OWLEntity> getEntities(@Nonnull String entityName) {
    var args = Parameters.forShortFormsIndex(projectId, branchId, entityName);
    return session.readTransaction(tx -> {
      var mutableDictionaryMap = Maps.<DictionaryLanguage, OWLEntity>newHashMap();
      var result = tx.run(SHORT_FORMS_INDEX_QUERY, args);
      while (result.hasNext()) {
        var row = result.next().asMap();
        var entityNode = (Node) row.get("entity");
        var propertyNode = (Node) row.get("annotationProperty");
        var literalNode = (Node) row.get("value");
        var entity = nodeTranslator.getOwlEntity(entityNode);
        var dictionaryLanguage = nodeTranslator.getDictionaryLanguage(propertyNode, literalNode);
        mutableDictionaryMap.put(dictionaryLanguage, entity);
      }
      return ImmutableMap.copyOf(mutableDictionaryMap);
    });
  }
}
