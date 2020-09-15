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
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AssertionAxiomBySubjectAccessor {

  @Nonnull
  Set<OWLClassAssertionAxiom>
  getClassAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
                                        @Nonnull ProjectId projectId,
                                        @Nonnull BranchId branchId,
                                        @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
                                      @Nonnull ProjectId projectId,
                                      @Nonnull BranchId branchId,
                                      @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(@Nonnull OWLAnnotationSubject owlAnnotationSubject,
                                    @Nonnull OWLAnnotationProperty property,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  default Set<OWLAxiom>
  getPropertyAssertionsForSubject(@Nonnull OWLIndividual owlIndividual,
                                  @Nonnull ProjectId projectId,
                                  @Nonnull BranchId branchId,
                                  @Nonnull OntologyDocumentId ontoDocId) {
    var annotationAssertions = getAnnotationAssertionAxioms(owlIndividual, projectId, branchId, ontoDocId);
    var objectPropertyAssertions = getObjectPropertyAssertionsForSubject(owlIndividual, projectId, branchId, ontoDocId);
    var dataPropertyAssertions = getDataPropertyAssertionsForSubject(owlIndividual, projectId, branchId, ontoDocId);
    return Stream
        .of(annotationAssertions.stream(),
            dataPropertyAssertions.stream(),
            objectPropertyAssertions.stream())
        .flatMap(ax -> ax)
        .collect(ImmutableSet.toImmutableSet());
  }

  private Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionAxioms(@Nonnull OWLIndividual owlIndividual,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId) {
    var annotationSubject = (owlIndividual.isNamed())
        ? owlIndividual.asOWLNamedIndividual().getIRI()
        : owlIndividual.asOWLAnonymousIndividual();
    return getAnnotationAssertionsForSubject(annotationSubject, projectId, branchId, ontoDocId);
  }
}
