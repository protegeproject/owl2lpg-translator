package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ShortFormIndexImpl implements ShortFormIndex {

  @Nonnull
  private final ImmutableList<ShortForm> shortFormList;

  public ShortFormIndexImpl(@Nonnull ImmutableList<ShortForm> shortFormList) {
    this.shortFormList = checkNotNull(shortFormList);
  }

  @Override
  public String getShortForm(DictionaryLanguage dictionaryLanguage, String defaultShortForm) {
    return shortFormList
        .stream()
        .filter(shortForm -> shortForm.getDictionaryLanguage().equals(dictionaryLanguage))
        .map(ShortForm::getShortForm)
        .findFirst()
        .orElse(defaultShortForm);
  }

  @Override
  public ShortFormIndex orderBy(List<DictionaryLanguage> dictLangPriorityList) {
    var orderedNameIndex = dictLangPriorityList
        .stream()
        .map(prioritizedDict -> shortFormList
            .stream()
            .filter(shortForm -> shortForm.getDictionaryLanguage().equals(prioritizedDict))
            .findFirst().orElse(null))
        .filter(Objects::nonNull)
        .collect(ImmutableList.toImmutableList());
    return new ShortFormIndexImpl(orderedNameIndex);
  }

  @Override
  public Optional<String> getFirst() {
    return shortFormList
        .stream()
        .map(ShortForm::getShortForm)
        .findFirst();
  }

  @Override
  public ImmutableMap<DictionaryLanguage, String> asImmutableMap() {
    return shortFormList
        .stream()
        .collect(ImmutableMap.toImmutableMap(
            ShortForm::getDictionaryLanguage,
            ShortForm::getShortForm,
            (dictLang1, dictLang2) -> dictLang1));
  }

  public static class Builder {

    private final List<ShortForm> mutableShortFormList = Lists.newArrayList();

    private void add(Node annotationPropertyNode, Node literalNode) {
      var propertyIri = IRI.create(annotationPropertyNode.get(PropertyFields.IRI).asString());
      var language = literalNode.hasLabel(PropertyFields.LANGUAGE)
          ? literalNode.get(PropertyFields.LANGUAGE).asString()
          : "";
      var dictionaryLanguage = DictionaryLanguage.create(propertyIri, language);
      var literalValue = literalNode.get(PropertyFields.LEXICAL_FORM).asString();
      var shortForm = ShortForm.get(dictionaryLanguage, literalValue);
      mutableShortFormList.add(shortForm);
    }

    public ShortFormIndex build() {
      return new ShortFormIndexImpl(
          ImmutableList.copyOf(mutableShortFormList));
    }
  }
}
