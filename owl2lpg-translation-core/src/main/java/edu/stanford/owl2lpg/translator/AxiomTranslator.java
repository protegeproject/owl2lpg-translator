package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.AxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 axioms.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomTranslator {

  @Nonnull
  private final Provider<AxiomVisitor> visitor;

  @Inject
  public AxiomTranslator(@Nonnull Provider<AxiomVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLAxiom ax) {
    checkNotNull(ax);
    return ax.accept(visitor.get());
  }
}
