package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.VersionedAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VersionedAxiomTranslator {

  @Nonnull
  private final Provider<VersionedAxiomVisitor> visitor;

  @Inject
  public VersionedAxiomTranslator(@Nonnull Provider<VersionedAxiomVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLAxiom axiom) {
    checkNotNull(axiom);
    return axiom.accept(visitor.get());
  }
}
