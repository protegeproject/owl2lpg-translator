package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomByEntityAccessor {

  Set<OWLSubClassOfAxiom> getSubClassOfAxiomsBySubClass(AxiomContext context, OWLClass subClass);
}
