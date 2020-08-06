package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.AxiomInProjectVisitor;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomInProjectTranslator {

  @Nonnull
  private final Provider<AxiomInProjectVisitor> visitor;

  @Inject
  public AxiomInProjectTranslator(@Nonnull Provider<AxiomInProjectVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLAxiom axiom) {
    checkNotNull(axiom);
    return axiom.accept(visitor.get());
  }
}
