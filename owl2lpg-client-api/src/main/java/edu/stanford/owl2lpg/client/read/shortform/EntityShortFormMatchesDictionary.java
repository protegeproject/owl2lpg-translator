package edu.stanford.owl2lpg.client.read.shortform;

import com.google.auto.value.AutoValue;
import com.google.common.collect.*;
import edu.stanford.bmir.protege.web.server.shortform.EntityShortFormMatches;
import edu.stanford.bmir.protege.web.server.shortform.ShortFormMatch;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class EntityShortFormMatchesDictionary {

  public static EntityShortFormMatchesDictionary get(@Nonnull ImmutableMultimap<DictionaryLanguage, EntityShortFormMatches> matchesDictionary) {
    return new AutoValue_EntityShortFormMatchesDictionary(matchesDictionary);
  }

  public abstract Multimap<DictionaryLanguage, EntityShortFormMatches> asMultimap();

  public Stream<EntityShortFormMatches> get(DictionaryLanguage language) {
    return asMultimap().get(language).stream();
  }

  public static class Builder {

    private final Map<DictionaryLanguage, Multimap<OWLEntity, ShortFormMatch>> dictionaryMap = Maps.newHashMap();

    public Builder() {
    }

    public Builder add(DictionaryLanguage language, ShortFormMatch shortFormMatch) {
      var entityShortFormMap = dictionaryMap.get(language);
      if (entityShortFormMap == null) {
        entityShortFormMap = HashMultimap.create();
        dictionaryMap.put(language, entityShortFormMap);
      }
      entityShortFormMap.put(shortFormMatch.getEntity(), shortFormMatch);
      return this;
    }

    public EntityShortFormMatchesDictionary build() {
      var multimapBuilder = ImmutableMultimap.<DictionaryLanguage, EntityShortFormMatches>builder();
      for (var language : dictionaryMap.keySet()) {
        var entityShortFormMap = dictionaryMap.get(language);
        for (var entity : entityShortFormMap.keySet()) {
          var shortFormList = ImmutableList.copyOf(entityShortFormMap.get(entity));
          multimapBuilder.put(language, EntityShortFormMatches.get(entity, shortFormList));
        }
      }
      return EntityShortFormMatchesDictionary.get(multimapBuilder.build());
    }
  }
}
