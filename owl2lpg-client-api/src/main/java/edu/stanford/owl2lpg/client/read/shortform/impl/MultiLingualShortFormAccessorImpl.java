package edu.stanford.owl2lpg.client.read.shortform.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.pagination.PageCollector;
import edu.stanford.bmir.protege.web.server.shortform.EntityShortFormMatches;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatch;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatchPosition;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.client.read.entity.EntityNodeMapper;
import edu.stanford.owl2lpg.client.read.shortform.MultiLingualShortFormAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class MultiLingualShortFormAccessorImpl implements MultiLingualShortFormAccessor {

  private static final String SHORT_FORMS_DICTIONARY_QUERY_FILE = "shortforms/short-forms-dictionary.cpy";
  private static final String SHORT_FORMS_INDEX_QUERY_FILE = "shortforms/short-forms-index.cpy";
  private static final String FULL_TEXT_SEARCH_BY_ANNOTATION_ASSERTION_QUERY_FILE =
      "shortforms/full-text-search-by-annotation-assertion.cpy";
  private static final String FULL_TEXT_SEARCH_BY_LOCAL_NAME_QUERY_FILE =
      "shortforms/full-text-search-by-local-name.cpy";

  private static final String SHORT_FORMS_DICTIONARY_QUERY = read(SHORT_FORMS_DICTIONARY_QUERY_FILE);
  private static final String SHORT_FORMS_INDEX_QUERY = read(SHORT_FORMS_INDEX_QUERY_FILE);
  private static final String FULL_TEXT_SEARCH_BY_ANNOTATION_ASSERTION_QUERY =
      read(FULL_TEXT_SEARCH_BY_ANNOTATION_ASSERTION_QUERY_FILE);
  private static final String FULL_TEXT_SEARCH_BY_LOCAL_NAME_QUERY =
      read(FULL_TEXT_SEARCH_BY_LOCAL_NAME_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final EntityNodeMapper entityNodeMapper;

  @Nonnull
  private final ObjectMapper objectNodeMapper;

  @Inject
  public MultiLingualShortFormAccessorImpl(@Nonnull Driver driver,
                                           @Nonnull EntityNodeMapper entityNodeMapper,
                                           @Nonnull ObjectMapper objectNodeMapper) {
    this.driver = checkNotNull(driver);
    this.entityNodeMapper = checkNotNull(entityNodeMapper);
    this.objectNodeMapper = checkNotNull(objectNodeMapper);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntity> getEntities(@Nonnull String entityName,
                                             @Nonnull List<DictionaryLanguage> languages,
                                             @Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId) {
    var entityMap = getEntities(entityName, projectId, branchId);
    return languages
        .stream()
        .map(language -> entityMap.getOrDefault(language, null))
        .filter(Objects::nonNull)
        .distinct()
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  @Override
  public String getShortForm(@Nonnull OWLEntity owlEntity,
                             @Nonnull List<DictionaryLanguage> languages,
                             @Nonnull String defaultShortForm,
                             @Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId) {
    var dictionaryMap = getShortForms(owlEntity, projectId, branchId);
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
                                                                @Nonnull List<DictionaryLanguage> languages,
                                                                @Nonnull ProjectId projectId,
                                                                @Nonnull BranchId branchId) {
    var dictionaryMap = getShortForms(owlEntity, projectId, branchId);
    return languages
        .stream()
        .filter(dictionaryMap::containsKey)
        .collect(ImmutableMap.toImmutableMap(
            language -> language,
            dictionaryMap::get));
  }

  @Nonnull
  @Override
  public Page<EntityShortFormMatches> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                              @Nonnull Set<EntityType<?>> entityTypes,
                                                              @Nonnull List<DictionaryLanguage> languages,
                                                              @Nonnull PageRequest pageRequest,
                                                              @Nonnull ProjectId projectId,
                                                              @Nonnull BranchId branchId) {
    var entityMatchesByAnnotationAssertion = getEntityShortFormMatches(
        FULL_TEXT_SEARCH_BY_ANNOTATION_ASSERTION_QUERY, searchStrings, projectId, branchId);
    var entityMatchesByLocalName = getEntityShortFormMatches(
        FULL_TEXT_SEARCH_BY_LOCAL_NAME_QUERY, searchStrings, projectId, branchId);
    return Streams.concat(
        languages
            .stream()
            .flatMap(entityMatchesByAnnotationAssertion::get)
            .filter(shortForm -> entityTypes.contains(shortForm.getEntity().getEntityType())),
        languages
            .stream()
            .flatMap(entityMatchesByLocalName::get)
            .filter(shortForm -> entityTypes.contains(shortForm.getEntity().getEntityType())))
        .distinct()
        .collect(PageCollector.toPage(
            pageRequest.getPageNumber(),
            pageRequest.getPageSize()))
        .orElse(Page.emptyPage());
  }

  @Nonnull
  public ImmutableMap<DictionaryLanguage, OWLEntity> getEntities(@Nonnull String entityName,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var dictionary = Maps.<DictionaryLanguage, OWLEntity>newHashMap();
        var inputParams = Parameters.forEntityName(entityName, projectId, branchId);
        var result = tx.run(SHORT_FORMS_INDEX_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var entityNode = (Node) row.get("entity");
          var entity = getOwlEntity(entityNode);
          var dictLangObject = row.get("dictionaryLanguage");
          var dictLanguage = getDictLanguage(dictLangObject);
          dictionary.put(dictLanguage, entity);
        }
        return ImmutableMap.copyOf(dictionary);
      });
    }
  }

  @Nonnull
  private ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull OWLEntity owlEntity,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var dictionary = Maps.<DictionaryLanguage, String>newHashMap();
        var inputParams = Parameters.forEntityIri(owlEntity.getIRI(), projectId, branchId);
        var result = tx.run(SHORT_FORMS_DICTIONARY_QUERY, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var dictLangObject = row.get("dictionaryLanguage");
          var dictLanguage = getDictLanguage(dictLangObject);
          var shortForm = (String) row.get("shortForm");
          dictionary.put(dictLanguage, shortForm);
        }
        return ImmutableMap.copyOf(dictionary);
      });
    }
  }

  @Nonnull
  private EntityShortFormMatchesDictionary getEntityShortFormMatches(@Nonnull String queryString,
                                                                     @Nonnull List<SearchString> searchStrings,
                                                                     @Nonnull ProjectId projectId,
                                                                     @Nonnull BranchId branchId) {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        var dictionaryBuilder = new EntityShortFormMatchesDictionary.Builder();
        var inputParams = Parameters.forSearchStrings(searchStrings, projectId, branchId);
        var result = tx.run(queryString, inputParams);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var entityNode = (Node) row.get("entity");
          var owlEntity = getOwlEntity(entityNode);
          var shortForm = (String) row.get("shortForm");
          var dictLangObject = row.get("dictionaryLanguage");
          var dictLanguage = getDictLanguage(dictLangObject);
          var shortFormMatch = getShortFormMatch(owlEntity, shortForm, searchStrings, dictLanguage);
          dictionaryBuilder.add(dictLanguage, shortFormMatch);
        }
        return dictionaryBuilder.build();
      });
    }
  }

  @Nonnull
  private ShortFormMatch getShortFormMatch(OWLEntity entity, String shortForm,
                                           List<SearchString> searchStrings,
                                           DictionaryLanguage language) {
    var matchPositions = Lists.<ShortFormMatchPosition>newArrayList();
    var lowerCasedShortForm = shortForm.toLowerCase();
    for (var ss : searchStrings) {
      var searchString = ss.getSearchString();
      for (int index = lowerCasedShortForm.indexOf(searchString);
           index >= 0;
           index = lowerCasedShortForm.indexOf(searchString, index + 1))
        matchPositions.add(ShortFormMatchPosition.get(index, index + searchString.length()));
    }
    return ShortFormMatch.get(entity, shortForm, language, ImmutableList.copyOf(matchPositions));
  }

  @Nonnull
  private OWLEntity getOwlEntity(Node entityNode) {
    return entityNodeMapper.toOwlEntity(entityNode);
  }

  @Nonnull
  private DictionaryLanguage getDictLanguage(Object dictLangObject) {
    return objectNodeMapper.convertValue(dictLangObject, DictionaryLanguage.class);
  }
}
