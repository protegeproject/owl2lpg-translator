package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAnnotationValueVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationValueTranslator {

  @Nonnull
  private final OWLAnnotationValueVisitorEx<Translation> visitor;

  @Inject
  public AnnotationValueTranslator(@Nonnull OWLAnnotationValueVisitorEx<Translation> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotationValue value) {
    checkNotNull(value);
    return value.accept(visitor);
  }
}
