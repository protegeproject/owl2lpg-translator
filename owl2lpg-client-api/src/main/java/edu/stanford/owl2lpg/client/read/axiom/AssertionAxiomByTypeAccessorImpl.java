package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomByTypeAccessorImpl implements AssertionAxiomByTypeAccessor {

  @Nonnull
  @Override
  public Set<OWLClassAssertionAxiom> getClassAssertionForType(OWLClass owlClass, AxiomContext context) {
    return null;
  }
}