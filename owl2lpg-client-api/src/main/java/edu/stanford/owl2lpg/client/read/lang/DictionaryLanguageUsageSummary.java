package edu.stanford.owl2lpg.client.read.lang;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.shared.shortform.AnnotationAssertionDictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import org.semanticweb.owlapi.model.IRI;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DictionaryLanguageUsageSummary {

  public static DictionaryLanguageUsageSummary get(ImmutableMap<DictionaryLanguage, Integer> summaryMap) {
    return new AutoValue_DictionaryLanguageUsageSummary(summaryMap);
  }

  public abstract ImmutableMap<DictionaryLanguage, Integer> asMap();

  public Stream<Map.Entry<DictionaryLanguage, Integer>> stream() {
    return asMap().entrySet().stream();
  }

  public int getCount(IRI propertyIri, String language) {
    var dictLanguage = AnnotationAssertionDictionaryLanguage.get(propertyIri, language);
    return asMap().get(dictLanguage);
  }

  public int getCount(IRI propertyIri) {
    return asMap().entrySet()
        .stream()
        .map(entry -> {
          var dictLanguage = entry.getKey();
          if (dictLanguage instanceof AnnotationAssertionDictionaryLanguage) {
            var storedPropertyIri = ((AnnotationAssertionDictionaryLanguage) dictLanguage);
            if (storedPropertyIri.equals(propertyIri)) {
              return entry.getValue();
            }
          }
          return 0;
        })
        .mapToInt(Integer::intValue)
        .sum();
  }

  public int getCount(String language) {
    return asMap().entrySet()
        .stream()
        .map(entry -> {
          var dictLanguage = entry.getKey();
          var storedLanguage = dictLanguage.getLang();
          if (storedLanguage.equals(language)) {
            return entry.getValue();
          }
          return 0;
        })
        .mapToInt(Integer::intValue)
        .sum();
  }

  public static class Builder {

    private final Map<DictionaryLanguage, Integer> mutableMap = Maps.newHashMap();

    public Builder put(DictionaryLanguage dictLang, int occurrence) {
      mutableMap.put(dictLang, occurrence);
      return this;
    }

    public DictionaryLanguageUsageSummary build() {
      return get(ImmutableMap.copyOf(mutableMap));
    }
  }
}
