package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
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
  getClassAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext);

  @Nonnull
  Set<OWLObjectPropertyAssertionAxiom>
  getObjectPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext);

  @Nonnull
  Set<OWLDataPropertyAssertionAxiom>
  getDataPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext);

  @Nonnull
  Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(OWLAnnotationSubject owlAnnotationSubject, AxiomContext axiomContext);

  @Nonnull
  Set<OWLAnnotationAssertionAxiom>
  getAnnotationAssertionsForSubject(OWLAnnotationSubject owlAnnotationSubject, OWLAnnotationProperty property, AxiomContext axiomContext);

  @Nonnull
  default Set<OWLAxiom> getPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext axiomContext) {
    var annotationAssertions = getAnnotationAssertionAxioms(owlIndividual, axiomContext);
    var objectPropertyAssertions = getObjectPropertyAssertionsForSubject(owlIndividual, axiomContext);
    var dataPropertyAssertions = getDataPropertyAssertionsForSubject(owlIndividual, axiomContext);
    return Stream
        .of(annotationAssertions.stream(),
            dataPropertyAssertions.stream(),
            objectPropertyAssertions.stream())
        .flatMap(ax -> ax)
        .collect(ImmutableSet.toImmutableSet());
  }

  private Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(OWLIndividual owlIndividual, AxiomContext axiomContext) {
    var annotationSubject = (owlIndividual.isNamed())
        ? owlIndividual.asOWLNamedIndividual().getIRI()
        : owlIndividual.asOWLAnonymousIndividual();
    return getAnnotationAssertionsForSubject(annotationSubject, axiomContext);
  }
}
