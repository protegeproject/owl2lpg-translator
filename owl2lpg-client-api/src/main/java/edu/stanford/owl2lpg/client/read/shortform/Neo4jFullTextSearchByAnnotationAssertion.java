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
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jFullTextSearchByAnnotationAssertion implements Neo4jFullTextSearch {

  private static final String SEARCHABLE_SHORT_FORMS_QUERY_FILE = "shortforms/full-text-search-by-annotation-assertion.cpy";
  private static final String SEARCHABLE_SHORT_FORMS_QUERY = read(SEARCHABLE_SHORT_FORMS_QUERY_FILE);

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final Neo4jResultMapper resultMapper;

  @Inject
  public Neo4jFullTextSearchByAnnotationAssertion(@Nonnull ProjectId projectId,
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
  public EntityShortFormMatchesDictionary getShortFormsContaining(List<SearchString> searchStrings) {
    try (var session = driver.session()) {
      var args = Parameters.forShortFormsContaining(projectId, branchId, searchStrings);
      return session.readTransaction(tx -> {
        var dictionaryBuilder = new EntityShortFormMatchesDictionary.Builder();
        var result = tx.run(SEARCHABLE_SHORT_FORMS_QUERY, args);
        while (result.hasNext()) {
          var row = result.next().asMap();
          var entity = resultMapper.getOwlEntity(row.get("entity"));
          var shortForm = resultMapper.getShortForm(row.get("shortForm"));
          var dictLanguage = resultMapper.getDictionaryLanguage(row.get("dictionaryLanguage"));
          var shortFormMatch = getShortFormMatch(entity, shortForm, searchStrings, dictLanguage);
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
}
