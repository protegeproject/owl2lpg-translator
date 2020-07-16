package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Node;
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
  private final Neo4jNodeTranslator nodeTranslator;

  @Inject
  public Neo4jMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull Driver driver,
                                              @Nonnull Neo4jNodeTranslator nodeTranslator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.driver = checkNotNull(driver);
    this.nodeTranslator = checkNotNull(nodeTranslator);
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
      var args = Parameters.forShortFormsDictionary(projectId, branchId, owlEntity.getIRI());
      return session.readTransaction(tx -> {
        var mutableDictionaryMap = Maps.<DictionaryLanguage, String>newHashMap();
        var result = tx.run(SHORT_FORMS_DICTIONARY_QUERY, args);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var propertyNode = (Node) row.get("annotationProperty");
          var literalNode = (Node) row.get("value");
          var dictionaryLanguage = nodeTranslator.getDictionaryLanguage(propertyNode, literalNode);
          var shortForm = nodeTranslator.getShortForm(literalNode);
          mutableDictionaryMap.put(dictionaryLanguage, shortForm);
        }
        return ImmutableMap.copyOf(mutableDictionaryMap);
      });
    }
  }
}
