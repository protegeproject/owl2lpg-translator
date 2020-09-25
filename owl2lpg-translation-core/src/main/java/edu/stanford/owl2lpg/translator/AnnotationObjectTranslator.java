package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.visitors.AnnotationObjectVisitor;
import org.semanticweb.owlapi.model.OWLAnnotation;

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
  private final Provider<AnnotationObjectVisitor> visitor;

  @Inject
  public AnnotationObjectTranslator(@Nonnull Provider<AnnotationObjectVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotation annotation) {
    checkNotNull(annotation);
    return annotation.accept(visitor.get());
  }
}
