package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatch;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatchPosition;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.Parameters;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jFullTextSearchByAnnotationValue implements Neo4jFullTextSearch {

  private static final String SEARCHABLE_SHORT_FORMS_QUERY_FILE = "shortforms/full-text-search-by-annotation-value.cpy";
  private static final String SEARCHABLE_SHORT_FORMS_QUERY = read(SEARCHABLE_SHORT_FORMS_QUERY_FILE);

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final Neo4jFullTextIndexName fullTextIndexName;

  @Nonnull
  private final Neo4jNodeTranslator nodeTranslator;

  @Inject
  public Neo4jFullTextSearchByAnnotationValue(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull Driver driver,
                                              @Nonnull @Named("annotationValueFullTextIndexName") Neo4jFullTextIndexName fullTextIndexName,
                                              @Nonnull Neo4jNodeTranslator nodeTranslator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.driver = checkNotNull(driver);
    this.fullTextIndexName = checkNotNull(fullTextIndexName);
    this.nodeTranslator = checkNotNull(nodeTranslator);
  }

  @Nonnull
  @Override
  public EntityShortFormMatchesDictionary getShortFormsContaining(List<SearchString> searchStrings) {
    try (var session = driver.session()) {
      var args = Parameters.forShortFormsContaining(projectId, branchId, fullTextIndexName, searchStrings);
      return session.readTransaction(tx -> {
        var dictionaryBuilder = new EntityShortFormMatchesDictionary.Builder();
        var result = tx.run(SEARCHABLE_SHORT_FORMS_QUERY, args);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var entityNode = (Node) row.get("entity");
          var propertyNode = (Node) row.get("annotationProperty");
          var literalNode = (Node) row.get("value");
          var entity = nodeTranslator.getOwlEntity(entityNode);
          var shortForm = nodeTranslator.getShortForm(literalNode);
          var language = nodeTranslator.getDictionaryLanguage(propertyNode, literalNode);
          var shortFormMatch = getShortFormMatch(entity, shortForm, searchStrings, language);
          dictionaryBuilder.add(language, shortFormMatch);
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
    for (var ss : searchStrings) {
      var searchString = ss.getSearchString();
      var matchIndex = shortForm.toLowerCase().indexOf(searchString);
      if (matchIndex != -1) {
        matchPositions.add(ShortFormMatchPosition.get(matchIndex, matchIndex + searchString.length()));
      }
    }
    return ShortFormMatch.get(entity, shortForm, language, ImmutableList.copyOf(matchPositions));
  }
}
