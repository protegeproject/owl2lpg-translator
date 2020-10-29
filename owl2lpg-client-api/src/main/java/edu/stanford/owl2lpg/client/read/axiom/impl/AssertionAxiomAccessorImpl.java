package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.AnnotationAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.ClassAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.DataPropertyAssertionAxiomAccessor;
import edu.stanford.owl2lpg.client.read.axiom.ObjectPropertyAssertionAxiomAccessor;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomAccessorImpl implements AssertionAxiomAccessor {

  @Nonnull
  private final DataPropertyAssertionAxiomAccessor dataPropertyAssertionAxiomAccessor;

  @Nonnull
  private final ObjectPropertyAssertionAxiomAccessor objectPropertyAssertionAxiomAccessor;

  @Nonnull
  private final ClassAssertionAxiomAccessor classAssertionAxiomAccessor;

  @Nonnull
  private final AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor;

  @Inject
  public AssertionAxiomAccessorImpl(@Nonnull DataPropertyAssertionAxiomAccessor dataPropertyAssertionAxiomAccessor,
                                    @Nonnull ObjectPropertyAssertionAxiomAccessor objectPropertyAssertionAxiomAccessor,
                                    @Nonnull ClassAssertionAxiomAccessor classAssertionAxiomAccessor,
                                    @Nonnull AnnotationAssertionAxiomAccessor annotationAssertionAxiomAccessor) {
    this.dataPropertyAssertionAxiomAccessor = checkNotNull(dataPropertyAssertionAxiomAccessor);
    this.objectPropertyAssertionAxiomAccessor = checkNotNull(objectPropertyAssertionAxiomAccessor);
    this.classAssertionAxiomAccessor = checkNotNull(classAssertionAxiomAccessor);
    this.annotationAssertionAxiomAccessor = checkNotNull(annotationAssertionAxiomAccessor);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLClassAssertionAxiom>
  getClassAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                              @Nonnull ProjectId projectId,
                              @Nonnull BranchId branchId,
                              @Nonnull OntologyDocumentId ontoDocId) {
    return classAssertionAxiomAccessor.getAxiomsBySubject(owlIndividual, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                       @Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId) {
    return objectPropertyAssertionAxiomAccessor.getAxiomsBySubject(owlIndividual, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId) {
    return dataPropertyAssertionAxiomAccessor.getAxiomsBySubject(owlIndividual, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId) {
    return annotationAssertionAxiomAccessor.getAxiomsBySubject(owlAnnotationSubject, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                   @Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId) {
    return annotationAssertionAxiomAccessor.getAxiomsBySubjectAndProperty(owlAnnotationSubject, owlAnnotationProperty,
        projectId, branchId, ontoDocId);
  }
}
