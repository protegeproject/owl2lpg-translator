package edu.stanford.owl2lpg.client.write.handlers;

import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomChangeHandlerImpl implements AxiomChangeHandler {

  @Nonnull
  private final AddAxiomHandler addAxiomHandler;

  @Inject
  public AxiomChangeHandlerImpl(@Nonnull AddAxiomHandler addAxiomHandler) {
    this.addAxiomHandler = checkNotNull(addAxiomHandler);
  }

  @Override
  public void handleAdd(@Nonnull OWLAxiom axiom) {
    addAxiomHandler.handle(axiom);
  }

  @Override
  public void handleRemove(@Nonnull OWLAxiom axiom) {

  }
}
