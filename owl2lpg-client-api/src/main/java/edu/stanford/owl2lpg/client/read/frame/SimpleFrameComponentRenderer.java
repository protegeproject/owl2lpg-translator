package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import edu.stanford.bmir.protege.web.shared.entity.*;
import edu.stanford.bmir.protege.web.shared.frame.FrameComponentRenderer;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;
import edu.stanford.bmir.protege.web.shared.shortform.ShortForm;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SimpleFrameComponentRenderer implements FrameComponentRenderer {

  @Nonnull
  private final Multimap<IRI, ShortForm> dictionaryMap;

  @Inject
  public SimpleFrameComponentRenderer(@Nonnull Multimap<IRI, ShortForm> dictionaryMap) {
    this.dictionaryMap = checkNotNull(dictionaryMap);
  }

  @Nonnull
  @Override
  public OWLClassData getRendering(@Nonnull OWLClass cls) {
    return OWLClassData.get(cls, getShortForms(cls.getIRI()));
  }

  @Nonnull
  @Override
  public OWLObjectPropertyData getRendering(@Nonnull OWLObjectProperty property) {
    return OWLObjectPropertyData.get(property, getShortForms(property.getIRI()));
  }

  @Nonnull
  @Override
  public OWLDataPropertyData getRendering(@Nonnull OWLDataProperty property) {
    return OWLDataPropertyData.get(property, getShortForms(property.getIRI()));
  }

  @Nonnull
  @Override
  public OWLAnnotationPropertyData getRendering(@Nonnull OWLAnnotationProperty property) {
    return OWLAnnotationPropertyData.get(property, getShortForms(property.getIRI()));
  }

  @Nonnull
  @Override
  public OWLNamedIndividualData getRendering(@Nonnull OWLNamedIndividual individual) {
    return OWLNamedIndividualData.get(individual, getShortForms(individual.getIRI()));
  }

  @Nonnull
  @Override
  public OWLDatatypeData getRendering(@Nonnull OWLDatatype datatype) {
    return OWLDatatypeData.get(datatype, getShortForms(datatype.getIRI()));
  }

  @Nonnull
  @Override
  public OWLLiteralData getRendering(@Nonnull OWLLiteral literal) {
    return OWLLiteralData.get(literal);
  }

  @Nonnull
  @Override
  public OWLPrimitiveData getRendering(@Nonnull OWLAnnotationValue annotationValue) {
    return null;
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntityData> getRendering(@Nonnull IRI iri) {
    return null;
  }

  @Nonnull
  @Override
  public OWLEntityData getEntityRendering(@Nonnull OWLEntity entity) {
    return null;
  }

  private ImmutableMap<DictionaryLanguage, String> getShortForms(@Nonnull IRI entityIri) {
    return dictionaryMap.get(entityIri)
        .stream()
        .collect(ImmutableMap.toImmutableMap(
            ShortForm::getDictionaryLanguage,
            ShortForm::getShortForm));
  }
}
