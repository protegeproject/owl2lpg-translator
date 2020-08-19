package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomBySubjectAccessor {

  Set<OWLAxiom> getAxiomForSubject(OWLClass subject, AxiomContext context);

  Set<OWLAxiom> getAxiomForSubject(OWLNamedIndividual subject, AxiomContext context);
}