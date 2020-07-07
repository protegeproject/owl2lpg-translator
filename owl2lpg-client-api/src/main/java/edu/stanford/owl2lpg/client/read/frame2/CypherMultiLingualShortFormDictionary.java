package edu.stanford.owl2lpg.client.read.frame2;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.read.axiom.CypherQueries.SHORT_FORMS_DICTIONARY_QUERY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherMultiLingualShortFormDictionary
    implements MultiLingualShortFormDictionary {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Session session;

  @Inject
  public CypherMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull Session session) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.session = checkNotNull(session);
  }

  @Nonnull
  @Override
  public String getShortForm(@Nonnull OWLEntity owlEntity,
                             @Nonnull List<DictionaryLanguage> languages,
                             @Nonnull String defaultShortForm) {
    var dictionaryMap = getShortForms(owlEntity, languages);
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
    var args = Parameters.forShortFormsDictionary(projectId, branchId, owlEntity.getIRI());
    return session.readTransaction(tx -> {
      var mutableDictionaryMap = Maps.<DictionaryLanguage, String>newHashMap();
      var result = tx.run(SHORT_FORMS_DICTIONARY_QUERY, args);
      while (result.hasNext()) {
        var row = result.next().asMap();
        var propertyNode = (Node) row.get("annotationProperty");
        var literalNode = (Node) row.get("value");
        var propertyIri = IRI.create(propertyNode.get(PropertyFields.IRI).asString());
        var language = literalNode.get(PropertyFields.LANGUAGE).asString();
        var shortForm = literalNode.get(PropertyFields.LEXICAL_FORM).asString();
        var dictionaryLanguage = DictionaryLanguage.create(propertyIri, language);
        mutableDictionaryMap.put(dictionaryLanguage, shortForm);
      }
      return ImmutableMap.copyOf(mutableDictionaryMap);
    });
  }
}
