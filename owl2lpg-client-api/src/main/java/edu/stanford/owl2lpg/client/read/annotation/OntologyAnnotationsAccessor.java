package edu.stanford.owl2lpg.client.read.annotation;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface OntologyAnnotationsAccessor {

  @Nonnull
  ImmutableSet<OWLAnnotation> getOntologyAnnotations(@Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAnnotationProperty> getOntologyAnnotationProperties(@Nonnull ProjectId projectId,
                                                                      @Nonnull BranchId branchId,
                                                                      @Nonnull OntologyDocumentId ontoDocId);

  boolean containsAnnotationInSignature(@Nonnull OWLAnnotation annotation,
                                        @Nonnull ProjectId projectId,
                                        @Nonnull BranchId branchId,
                                        @Nonnull OntologyDocumentId ontoDocId);
}
