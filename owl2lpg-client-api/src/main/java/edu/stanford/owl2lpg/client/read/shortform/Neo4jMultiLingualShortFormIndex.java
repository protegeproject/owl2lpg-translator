package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
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
  private final Driver driver;

  @Nonnull
  private final Neo4jResultMapper resultMapper;

  @Inject
  public Neo4jMultiLingualShortFormIndex(@Nonnull ProjectId projectId,
                                         @Nonnull BranchId branchId,
                                         @Nonnull Driver driver,
                                         @Nonnull Neo4jResultMapper resultMapper) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.driver = checkNotNull(driver);
    this.resultMapper = checkNotNull(resultMapper);
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
    try (var session = driver.session()) {
      var args = Parameters.forShortFormsIndex(projectId, branchId, entityName);
      var output = session.readTransaction(tx -> {
        var mutableDictionaryMap = Maps.<DictionaryLanguage, OWLEntity>newHashMap();
        var result = tx.run(SHORT_FORMS_INDEX_QUERY, args);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var dictLanguage = resultMapper.getDictionaryLanguage(row.get("dictionaryLanguage"));
          var entity = resultMapper.getOwlEntity(row.get("entity"));
          mutableDictionaryMap.put(dictLanguage, entity);
        }
        return mutableDictionaryMap;
      });
      return ImmutableMap.copyOf(output);
    }
  }
}
