package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.bmir.protege.web.server.change.AddAxiomChange;
import edu.stanford.bmir.protege.web.server.change.RemoveAxiomChange;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomChangeHandler {

  void handle(@Nonnull AddAxiomChange addAxiomChange);

  void handle(@Nonnull RemoveAxiomChange removeAxiomChange);
}
