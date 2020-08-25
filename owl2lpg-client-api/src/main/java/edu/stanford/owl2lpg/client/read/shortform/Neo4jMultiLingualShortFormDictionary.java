package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
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

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jMultiLingualShortFormDictionary implements MultiLingualShortFormDictionary {

  private static final String SHORT_FORMS_DICTIONARY_QUERY_FILE = "shortforms/short-forms-dictionary.cpy";
  private static final String SHORT_FORMS_DICTIONARY_QUERY = read(SHORT_FORMS_DICTIONARY_QUERY_FILE);

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final Neo4jResultMapper resultMapper;

  @Inject
  public Neo4jMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
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
  public String getShortForm(@Nonnull OWLEntity owlEntity,
                             @Nonnull List<DictionaryLanguage> languages,
                             @Nonnull String defaultShortForm) {
    var dictionaryMap = getShortForms(owlEntity);
    return languages
        .stream()
        .map(language -> dictionaryMap.getOrDefault(language, null))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(defaultShortForm);
  }

  @Nonnull
  @Override
  public ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull OWLEntity owlEntity,
                                                                @Nonnull List<DictionaryLanguage> languages) {
    var dictionaryMap = getShortForms(owlEntity);
    return languages
        .stream()
        .filter(dictionaryMap::containsKey)
        .collect(ImmutableMap.toImmutableMap(
            language -> language,
            dictionaryMap::get));
  }

  private ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull OWLEntity owlEntity) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var dictionary = ImmutableMap.<DictionaryLanguage, String>builder();
        var inputParams = Parameters.forEntityIri(owlEntity.getIRI(), projectId, branchId);
        var result = tx.run(SHORT_FORMS_DICTIONARY_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var dictLanguage = resultMapper.getDictionaryLanguage(row.get("dictionaryLanguage"));
          var shortForm = resultMapper.getShortForm(row.get("shortForm"));
          dictionary.put(dictLanguage, shortForm);
        }
        return dictionary.build();
      });
    }
  }
}
