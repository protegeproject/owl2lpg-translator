package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
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
public interface AssertionAxiomBySubjectAccessor {

  @Nonnull
  Stream<OWLClassAssertionAxiom>
  getClassAssertionForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext);

  @Nonnull
  Stream<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext);

  @Nonnull
  Stream<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext);

  @Nonnull
  Stream<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(OWLAnnotationSubject owlAnnotationSubject, AxiomContext axiomContext);

  @Nonnull
  Stream<OWLAnnotationAssertionAxiom>
  getProjectAnnotationAssertionAxiomsForSubject(OWLAnnotationSubject owlAnnotationSubject, AxiomContext axiomContext);

  @Nonnull
  default Stream<OWLAxiom> getPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext) {
    var annotationAssertions = getAnnotationAssertionAxioms(owlIndividual, axiomContext);
    var objectPropertyAssertions = getObjectPropertyAssertionsForSubject(owlIndividual, axiomContext);
    var dataPropertyAssertions = getDataPropertyAssertionsForSubject(owlIndividual, axiomContext);
    return Stream
        .of(annotationAssertions,
            dataPropertyAssertions,
            objectPropertyAssertions)
        .flatMap(ax -> ax);
  }

  private Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLIndividual owlIndividual, AxiomContext axiomContext) {
    var annotationSubject = (owlIndividual.isNamed())
        ? owlIndividual.asOWLNamedIndividual().getIRI()
        : owlIndividual.asOWLAnonymousIndividual();
    return getAnnotationAssertionsForSubject(annotationSubject, axiomContext);
  }
}
