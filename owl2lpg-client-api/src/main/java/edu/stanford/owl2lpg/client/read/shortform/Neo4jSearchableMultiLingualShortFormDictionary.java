package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.primitives.ImmutableIntArray;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatch;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.frame.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSearchableMultiLingualShortFormDictionary implements SearchableMultiLingualShortFormDictionary {

  private static final String SEARCHABLE_SHORT_FORMS_QUERY_FILE = "shortforms/searchable-short-forms-dictionary.cpy";
  private static final String SEARCHABLE_SHORT_FORMS_QUERY = read(SEARCHABLE_SHORT_FORMS_QUERY_FILE);

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Session session;

  @Nonnull
  private final FullTextIndexName annotationValueFullTextIndexName;

  @Nonnull
  private final Neo4jNodeTranslator nodeTranslator;

  @Inject
  public Neo4jSearchableMultiLingualShortFormDictionary(@Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull Session session,
                                                        @Nonnull FullTextIndexName annotationValueFullTextIndexName,
                                                        @Nonnull Neo4jNodeTranslator nodeTranslator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.session = checkNotNull(session);
    this.annotationValueFullTextIndexName = checkNotNull(annotationValueFullTextIndexName);
    this.nodeTranslator = checkNotNull(nodeTranslator);
  }

  @Nonnull
  @Override
  public Stream<ShortFormMatch> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                        @Nonnull Set<EntityType<?>> entityTypes,
                                                        @Nonnull List<DictionaryLanguage> languages) {
    var shortFormMatchMultiMap = getShortFormsContaining(searchStrings, entityTypes);
    return languages
        .stream()
        .flatMap(language -> shortFormMatchMultiMap.get(language).stream())
        .distinct();
  }

  @Nonnull
  public Multimap<DictionaryLanguage, ShortFormMatch> getShortFormsContaining(List<SearchString> searchStrings,
                                                                              Set<EntityType<?>> entityTypes) {
    var args = Parameters.forShortFormsContaining(
        projectId, branchId, annotationValueFullTextIndexName, searchStrings);
    return session.readTransaction(tx -> {
      var mutableDictionaryMap = HashMultimap.<DictionaryLanguage, ShortFormMatch>create();
      var result = tx.run(SEARCHABLE_SHORT_FORMS_QUERY, args);
      while (result.hasNext()) {
        var row = result.next().asMap();
        var entityNode = (Node) row.get("entity");
        var propertyNode = (Node) row.get("annotationProperty");
        var literalNode = (Node) row.get("value");
        var entity = nodeTranslator.getOwlEntity(entityNode);
        if (entityTypes.contains(entity.getEntityType())) {
          var shortForm = nodeTranslator.getShortForm(literalNode);
          var dictionaryLanguage = nodeTranslator.getDictionaryLanguage(propertyNode, literalNode);
          ShortFormMatch shortFormMatch = getShortFormMatch(entity, shortForm, searchStrings, dictionaryLanguage);
          mutableDictionaryMap.put(dictionaryLanguage, shortFormMatch);
        }
      }
      return ImmutableMultimap.copyOf(mutableDictionaryMap);
    });
  }

  @Nonnull
  private ShortFormMatch getShortFormMatch(OWLEntity entity, String shortForm,
                                           List<SearchString> searchStrings,
                                           DictionaryLanguage dictionaryLanguage) {
    var matchPositions = new int[searchStrings.size()];
    var matchCount = 0;
    for (var i = 0; i < searchStrings.size(); i++) {
      var searchString = searchStrings.get(i).getSearchString();
      var matchIndex = shortForm.toLowerCase().indexOf(searchString);
      matchPositions[i] = matchIndex;
      if (matchIndex != -1) {
        matchCount++;
      }
    }
    return ShortFormMatch.get(entity, shortForm, dictionaryLanguage, matchCount,
        ImmutableIntArray.copyOf(matchPositions));
  }
}
