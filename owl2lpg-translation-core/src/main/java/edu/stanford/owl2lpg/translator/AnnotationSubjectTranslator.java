package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationSubjectTranslator {

  @Nonnull
  private final Provider<OWLAnnotationSubjectVisitorEx<Translation>> visitor;

  @Inject
  public AnnotationSubjectTranslator(@Nonnull Provider<OWLAnnotationSubjectVisitorEx<Translation>> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotationSubject subject) {
    checkNotNull(subject);
    return subject.accept(visitor.get());
  }
}
