package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomSubjectAccessor {

  Set<OWLAxiom> getAxioms(AxiomContext context, OWLClass subject);
}
