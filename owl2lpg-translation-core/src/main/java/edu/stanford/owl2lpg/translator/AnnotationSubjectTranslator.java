package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationSubjectTranslator {

  @Nonnull
  private final OWLAnnotationSubjectVisitorEx<Translation> visitor;

  @Inject
  public AnnotationSubjectTranslator(@Nonnull OWLAnnotationSubjectVisitorEx<Translation> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotationSubject subject) {
    checkNotNull(subject);
    return subject.accept(visitor);
  }
}
