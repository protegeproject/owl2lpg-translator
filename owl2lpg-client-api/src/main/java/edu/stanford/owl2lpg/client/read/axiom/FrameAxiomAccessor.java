package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface FrameAxiomAccessor {

  Set<OWLAxiom> getFrameAxioms(AxiomContext context, OWLClass subject);
}