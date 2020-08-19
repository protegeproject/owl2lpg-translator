package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
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
public class AssertionAxiomBySubjectAccessorImpl implements AssertionAxiomBySubjectAccessor {

  @Nonnull
  @Override
  public Stream<OWLClassAssertionAxiom> getClassAssertionForSubject(OWLIndividual owlIndividual, AxiomContext context) {
    return null;
  }

  @Nonnull
  @Override
  public Stream<OWLObjectPropertyAssertionAxiom> getObjectPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext context) {
    return null;
  }

  @Nonnull
  @Override
  public Stream<OWLDataPropertyAssertionAxiom> getDataPropertyAssertionsForSubject(OWLIndividual owlIndividual, AxiomContext context) {
    return null;
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationAssertionAxiom> getAnnotationAssertionsForSubject(OWLAnnotationSubject owlAnnotationSubject, AxiomContext context) {
    return null;
  }

  @Nonnull
  @Override
  public Stream<OWLAnnotationAssertionAxiom> getProjectAnnotationAssertionAxiomsForSubject(OWLAnnotationSubject owlAnnotationSubject, AxiomContext context) {
    return null;
  }
}
