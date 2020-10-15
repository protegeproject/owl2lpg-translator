package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.client.write.GraphWriter;
import edu.stanford.owl2lpg.translator.AnnotationObjectTranslator;
import org.semanticweb.owlapi.model.OWLAnnotation;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class RemoveOntologyAnnotationHandler {

  @Nonnull
  private final GraphWriter graphWriter;

  @Nonnull
  private final AnnotationObjectTranslator annotationTranslator;

  @Nonnull
  private final TranslationTranslator translationTranslator;

  @Inject
  public RemoveOntologyAnnotationHandler(@Nonnull GraphWriter graphWriter,
                                         @Nonnull AnnotationObjectTranslator annotationTranslator,
                                         @Nonnull TranslationTranslator translationTranslator) {
    this.graphWriter = checkNotNull(graphWriter);
    this.annotationTranslator = checkNotNull(annotationTranslator);
    this.translationTranslator = checkNotNull(translationTranslator);
  }

  public void handle(@Nonnull OWLAnnotation annotation) {
    var translation = annotationTranslator.translate(annotation);
    var deleteQuery = translationTranslator.translateToCypherDeleteQuery(translation);
    deleteQuery.forEach(graphWriter::execute);
  }
}
