package edu.stanford.owl2lpg.client.write.handlers.impl;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.write.GraphWriter;
import edu.stanford.owl2lpg.client.write.TranslationTranslator;
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

  public void handle(@Nonnull ProjectId projectId,
                     @Nonnull BranchId branchId,
                     @Nonnull OntologyDocumentId documentId,
                     @Nonnull OWLAnnotation annotation) {
    var translation = annotationTranslator.translate(annotation);
    var deleteQuery = translationTranslator.translateToCypherDeleteQuery(projectId, branchId, documentId, translation);
    deleteQuery.forEach(graphWriter::execute);
  }
}
