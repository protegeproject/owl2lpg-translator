package edu.stanford.owl2lpg.client.read.axiom;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AssertionAxiomByTypeAccessorImpl implements AssertionAxiomByTypeAccessor {

  @Nonnull
  @Override
  public Stream<OWLClassAssertionAxiom> getClassAssertionForType(OWLClassExpression owlClassExpression, AxiomContext context) {
    return null;
  }
}
