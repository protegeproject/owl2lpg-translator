package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.visitors.AnnotationValueVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationValue;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationValueTranslator {

  @Nonnull
  private final Provider<AnnotationValueVisitor> visitor;

  @Inject
  public AnnotationValueTranslator(@Nonnull Provider<AnnotationValueVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotationValue value) {
    checkNotNull(value);
    return value.accept(visitor.get());
  }
}
