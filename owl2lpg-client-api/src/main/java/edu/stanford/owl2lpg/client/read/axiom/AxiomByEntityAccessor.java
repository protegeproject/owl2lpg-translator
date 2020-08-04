package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomByEntityAccessor {

  Set<OWLAxiom> getSubClassOfAxiomsBySubClass(AxiomContext context, OWLClass subClass);
}
