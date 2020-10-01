package edu.stanford.owl2lpg.client.bind.change;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.client.write.handlers.AxiomChangeHandler;
import edu.stanford.owl2lpg.client.write.handlers.AxiomChangeHandlerImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = AxiomTranslatorModule.class)
public abstract class AxiomChangeHandlerModule {

  @Binds
  @ProjectSingleton
  public abstract AxiomChangeHandler
  provideAxiomChangeHandler(AxiomChangeHandlerImpl impl);
}
