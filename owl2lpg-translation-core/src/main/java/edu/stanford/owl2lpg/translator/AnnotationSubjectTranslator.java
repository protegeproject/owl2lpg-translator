package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.visitors.AnnotationSubjectVisitor;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

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
  private final Provider<AnnotationSubjectVisitor> visitor;

  @Inject
  public AnnotationSubjectTranslator(@Nonnull Provider<AnnotationSubjectVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLAnnotationSubject subject) {
    checkNotNull(subject);
    return subject.accept(visitor.get());
  }
}
