package edu.stanford.owl2lpg.client.read.lang.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.lang.DictionaryLanguageAccessor;
import edu.stanford.owl2lpg.client.read.lang.DictionaryLanguageUsageSummary;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DictionaryLanguageAccessorImpl implements DictionaryLanguageAccessor {

  private static final String DICTIONARY_LANGUAGE_USAGE_QUERY_FILE = "read/lang/dictionary-language-usage.cpy";

  private static final String DICTIONARY_LANGUAGE_USAGE_QUERY = read(DICTIONARY_LANGUAGE_USAGE_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final ObjectMapper objectNodeMapper;

  @Inject
  public DictionaryLanguageAccessorImpl(@Nonnull Driver driver,
                                        @Nonnull ObjectMapper objectNodeMapper) {
    this.driver = checkNotNull(driver);
    this.objectNodeMapper = checkNotNull(objectNodeMapper);
  }

  @Override
  public DictionaryLanguageUsageSummary getUsageSummary(@Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    var inputParams = Parameters.forContext(projectId, branchId, ontoDocId);
    return getUsage(inputParams);
  }

  @Nonnull
  public DictionaryLanguageUsageSummary getUsage(Value inputParams) {
    try (var session = driver.session()) {
      var dictLanguageUsageBuilder = new DictionaryLanguageUsageSummary.Builder();
      return session.readTransaction(tx -> {
        var result = tx.run(DICTIONARY_LANGUAGE_USAGE_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var dictLangObject = row.get("dictionaryLanguage");
          var dictLanguage = getDictLanguage(dictLangObject);
          var count = (Long) row.get("count");
          dictLanguageUsageBuilder.put(dictLanguage, Math.toIntExact(count));
        }
        return dictLanguageUsageBuilder.build();
      });
    }
  }

  @Nonnull
  private DictionaryLanguage getDictLanguage(Object dictLangObject) {
    return objectNodeMapper.convertValue(dictLangObject, DictionaryLanguage.class);
  }
}
