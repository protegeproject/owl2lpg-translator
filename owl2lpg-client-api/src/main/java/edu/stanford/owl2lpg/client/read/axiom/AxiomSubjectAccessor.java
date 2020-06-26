package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomSubjectAccessor {

  AxiomSubject getAxiomSubject(AxiomContext context, OWLClass subject);
}
