package edu.stanford.owl2lpg.client.bind.shortform;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.shortform.*;
import edu.stanford.bmir.protege.web.shared.pagination.Page;
import edu.stanford.bmir.protege.web.shared.pagination.PageRequest;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jMultiLingualDictionary implements MultiLingualDictionary {

  @Nonnull
  private final MultiLingualShortFormIndex multiLingualShortFormIndex;

  @Nonnull
  private final MultiLingualShortFormDictionary multiLingualShortFormDictionary;

  @Nonnull
  private final SearchableMultiLingualShortFormDictionary searchableMultiLingualShortFormDictionary;

  @Inject
  public Neo4jMultiLingualDictionary(@Nonnull MultiLingualShortFormIndex multiLingualShortFormIndex,
                                     @Nonnull MultiLingualShortFormDictionary multiLingualShortFormDictionary,
                                     @Nonnull SearchableMultiLingualShortFormDictionary searchableMultiLingualShortFormDictionary) {
    this.multiLingualShortFormIndex = checkNotNull(multiLingualShortFormIndex);
    this.multiLingualShortFormDictionary = checkNotNull(multiLingualShortFormDictionary);
    this.searchableMultiLingualShortFormDictionary = checkNotNull(searchableMultiLingualShortFormDictionary);
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntities(@Nonnull String entityName,
                                       @Nonnull List<DictionaryLanguage> languages) {
    return multiLingualShortFormIndex.getEntities(entityName, languages);
  }

  @Nonnull
  @Override
  public String getShortForm(@Nonnull OWLEntity owlEntity,
                             @Nonnull List<DictionaryLanguage> languages,
                             @Nonnull String defaultShortForm) {
    return multiLingualShortFormDictionary.getShortForm(owlEntity, languages, defaultShortForm);
  }

  @Nonnull
  @Override
  public ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull OWLEntity owlEntity,
                                                                @Nonnull List<DictionaryLanguage> languages) {
    return multiLingualShortFormDictionary.getShortForms(owlEntity, languages);
  }

  @Nonnull
  @Override
  public Page<EntityShortFormMatches> getShortFormsContaining(@Nonnull List<SearchString> searchStrings,
                                                              @Nonnull Set<EntityType<?>> entityTypes,
                                                              @Nonnull List<DictionaryLanguage> languages,
                                                              @Nonnull PageRequest pageRequest) {
    return searchableMultiLingualShortFormDictionary.getShortFormsContaining(searchStrings, entityTypes, languages, pageRequest);
  }
}
