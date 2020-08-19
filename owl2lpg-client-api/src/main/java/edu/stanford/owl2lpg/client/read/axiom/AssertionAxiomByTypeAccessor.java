package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AssertionAxiomByTypeAccessor {

  @Nonnull
  Stream<OWLClassAssertionAxiom>
  getClassAssertionForType(OWLClass owlClass, AxiomContext axiomContext);
}
