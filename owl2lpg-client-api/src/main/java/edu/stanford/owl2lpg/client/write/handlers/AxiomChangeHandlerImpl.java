package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;

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

  @Nonnull
  private final RemoveAxiomHandler removeAxiomHandler;

  @Inject
  public AxiomChangeHandlerImpl(@Nonnull AddAxiomHandler addAxiomHandler,
                                @Nonnull RemoveAxiomHandler removeAxiomHandler) {
    this.addAxiomHandler = checkNotNull(addAxiomHandler);
    this.removeAxiomHandler = checkNotNull(removeAxiomHandler);
  }

  @Override
  public void handle(@Nonnull AddAxiomChange addAxiomChange) {
    var ontologyId = addAxiomChange.getOntologyId();
    var axiom = addAxiomChange.getAxiom();
    addAxiomHandler.handle(ontologyId, axiom);
  }

  @Override
  public void handle(@Nonnull RemoveAxiomChange removeAxiomChange) {
    var ontologyId = removeAxiomChange.getOntologyId();
    var axiom = removeAxiomChange.getAxiom();
    removeAxiomHandler.handle(ontologyId, axiom);
  }
}
