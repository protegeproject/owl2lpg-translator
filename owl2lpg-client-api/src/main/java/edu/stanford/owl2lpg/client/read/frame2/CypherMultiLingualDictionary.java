package edu.stanford.owl2lpg.client.read.frame2;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualDictionary;
import edu.stanford.bmir.protege.web.server.shortform.SearchString;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatch;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.owl2lpg.client.read.axiom.ShortFormAccessor;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherMultiLingualDictionary implements MultiLingualDictionary {

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final ShortFormAccessor shortFormAccessor;

  @Nonnull
//  private final DictionaryNameAccessor dictionaryNameAccessor;

  public CypherMultiLingualDictionary(@Nonnull AxiomContext axiomContext,
                                      @Nonnull ShortFormAccessor shortFormAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.shortFormAccessor = checkNotNull(shortFormAccessor);
  }

  @Override
  public void loadLanguages(@Nonnull List<DictionaryLanguage> dictLangList) {
  }

  @Nonnull
  @Override
  public String getShortForm(@Nonnull OWLEntity owlEntity,
                             @Nonnull List<DictionaryLanguage> priorityList,
                             @Nonnull String defaultShortForm) {
    return shortFormAccessor.getShortFormIndex(axiomContext, owlEntity.getIRI())
        .orderBy(priorityList)
        .getFirst()
        .orElse(defaultShortForm);
  }

  @Nonnull
  @Override
  public Stream<ShortFormMatch> getShortFormsContaining(@Nonnull List<SearchString> searchStringList,
                                                        @Nonnull Set<EntityType<?>> entityTypeSet,
                                                        @Nonnull List<DictionaryLanguage> priorityList) {
    return null;
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntities(@Nonnull String entityName,
                                       @Nonnull List<DictionaryLanguage> priorityList) {
    return null;
//    return dictionaryNameAccessor.getDictionaryNameIndex(axiomContext, entityName)
//        .orderBy(priorityList)
//        .getEntities(entityName)
//        .stream();
  }

  @Override
  public void update(@Nonnull Collection<OWLEntity> collection,
                     @Nonnull List<DictionaryLanguage> priorityList) {
  }

  @Nonnull
  @Override
  public ImmutableMap<DictionaryLanguage, String> getShortForms(OWLEntity owlEntity,
                                                                List<DictionaryLanguage> priorityList) {
    return shortFormAccessor.getShortFormIndex(axiomContext, owlEntity.getIRI())
        .orderBy(priorityList)
        .asImmutableMap();
  }
}
