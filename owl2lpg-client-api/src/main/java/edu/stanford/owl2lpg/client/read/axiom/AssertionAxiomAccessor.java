package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AssertionAxiomAccessor {

  @Nonnull
  ImmutableSet<OWLClassAssertionAxiom>
  getClassAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                              @Nonnull ProjectId projectId,
                              @Nonnull BranchId branchId,
                              @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                       @Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                     @Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsBySubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                   @Nonnull OWLAnnotationProperty property,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  default ImmutableSet<OWLAxiom>
  getPropertyAssertionsBySubject(@Nonnull OWLIndividual owlIndividual,
                                 @Nonnull ProjectId projectId,
                                 @Nonnull BranchId branchId,
                                 @Nonnull OntologyDocumentId ontoDocId) {
    var annotationAssertions = getAnnotationAssertionAxioms(owlIndividual, projectId, branchId, ontoDocId);
    var objectPropertyAssertions = getObjectPropertyAssertionsBySubject(owlIndividual, projectId, branchId, ontoDocId);
    var dataPropertyAssertions = getDataPropertyAssertionsBySubject(owlIndividual, projectId, branchId, ontoDocId);
    return Stream
        .of(annotationAssertions.stream(),
            dataPropertyAssertions.stream(),
            objectPropertyAssertions.stream())
        .flatMap(ax -> ax)
        .collect(ImmutableSet.toImmutableSet());
  }

  private ImmutableSet<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionAxioms(@Nonnull OWLIndividual owlIndividual,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId) {
    var annotationSubject = (owlIndividual.isNamed())
        ? owlIndividual.asOWLNamedIndividual().getIRI()
        : owlIndividual.asOWLAnonymousIndividual();
    return getAnnotationAssertionsBySubject(annotationSubject, projectId, branchId, ontoDocId);
  }
}
