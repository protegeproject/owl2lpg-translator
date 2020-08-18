package edu.stanford.owl2lpg.client.read.shortform;

import com.google.common.collect.Streams;
import edu.stanford.bmir.protege.web.server.pagination.PageCollector;
import edu.stanford.bmir.protege.web.server.shortform.EntityShortFormMatches;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jSearchableMultiLingualShortFormDictionary implements SearchableMultiLingualShortFormDictionary {

  @Nonnull
  private final Neo4jFullTextSearch searchEntityByAnnotationAssertion;

  @Nonnull
  private final Neo4jFullTextSearch searchEntityByLocalName;

  @Inject
  public Neo4jSearchableMultiLingualShortFormDictionary(@Nonnull @Named("fullTextSearchByAnnotationAssertion") Neo4jFullTextSearch searchEntityByAnnotationAssertion,
                                                        @Nonnull @Named("fullTextSearchByLocalName") Neo4jFullTextSearch searchEntityByLocalName) {
    this.searchEntityByAnnotationAssertion = checkNotNull(searchEntityByAnnotationAssertion);
    this.searchEntityByLocalName = checkNotNull(searchEntityByLocalName);
  }

  @Nonnull
  @Override
  public Page<EntityShortFormMatches> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                              @Nonnull Set<EntityType<?>> entityTypes,
                                                              @Nonnull List<DictionaryLanguage> languages,
                                                              @Nonnull PageRequest pageRequest) {
    var entityMatchesByAnnotationAssertion = searchEntityByAnnotationAssertion.getShortFormsContaining(searchStrings);
    var entityMatchesByLocalName = searchEntityByLocalName.getShortFormsContaining(searchStrings);
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
}
