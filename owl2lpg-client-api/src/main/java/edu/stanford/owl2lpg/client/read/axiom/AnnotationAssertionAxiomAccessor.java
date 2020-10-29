package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AnnotationAssertionAxiomAccessor {

  @Nonnull
  ImmutableSet<OWLAnnotationAssertionAxiom> getAxiomsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                                               @Nonnull ProjectId projectId,
                                                               @Nonnull BranchId branchId,
                                                               @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAnnotationAssertionAxiom> getAxiomsBySubjectAndProperty(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                                                          @Nonnull OWLAnnotationProperty property,
                                                                          @Nonnull ProjectId projectId,
                                                                          @Nonnull BranchId branchId,
                                                                          @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAnnotationAssertionAxiom> getAxiomsByValue(@Nonnull OWLAnnotationValue owlAnnotationValue,
                                                             @Nonnull ProjectId projectId,
                                                             @Nonnull BranchId branchId,
                                                             @Nonnull OntologyDocumentId ontoDocId);
}
