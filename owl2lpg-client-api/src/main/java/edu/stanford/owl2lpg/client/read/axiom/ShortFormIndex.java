package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableMap;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;

import java.util.List;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ShortFormIndex {

  String getShortForm(DictionaryLanguage dictionaryLanguage, String defaultShortForm);

  ShortFormIndex orderBy(List<DictionaryLanguage> dictLangPriorityList);

  Optional<String> getFirst();

  ImmutableMap<DictionaryLanguage, String> asImmutableMap();
}
