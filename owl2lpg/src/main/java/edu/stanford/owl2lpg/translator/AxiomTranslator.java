package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * A translator to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomTranslator {

  @Nonnull
  private final OWLAxiomVisitorEx<Translation> visitor;

  @Inject
  public AxiomTranslator(OWLAxiomVisitorEx<Translation> visitor) {
    this.visitor = visitor;
  }

  @Nonnull
  public Translation visit(OWLAxiom ax) {
    return ax.accept(visitor);
  }
}
