package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.AnnotationValueTranslator;
import edu.stanford.owl2lpg.translator.IndividualTranslator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubjectVisitorEx;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 annotation subjects.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationSubjectVisitor implements OWLAnnotationSubjectVisitorEx<Translation> {

  @Nonnull
  private final AnnotationValueTranslator annotationValueTranslator;

  @Nonnull
  private final IndividualTranslator individualTranslator;

  @Inject
  public AnnotationSubjectVisitor(@Nonnull AnnotationValueTranslator annotationValueTranslator,
                                  @Nonnull IndividualTranslator individualTranslator) {
    this.annotationValueTranslator = checkNotNull(annotationValueTranslator);
    this.individualTranslator = checkNotNull(individualTranslator);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull IRI iri) {
    return annotationValueTranslator.translate(iri);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnonymousIndividual individual) {
    return individualTranslator.translate(individual);
  }
}
