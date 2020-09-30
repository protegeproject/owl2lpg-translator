package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationObjectVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationObjectTranslator {

  @Nonnull
  private final Provider<OWLAnnotationObjectVisitorEx<Translation>> visitor;

  @Inject
  public AnnotationObjectTranslator(@Nonnull Provider<OWLAnnotationObjectVisitorEx<Translation>> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotation annotation) {
    checkNotNull(annotation);
    return annotation.accept(visitor.get());
  }
}
