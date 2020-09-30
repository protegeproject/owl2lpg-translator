package edu.stanford.owl2lpg.client.write.handlers;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.translator.TranslatorModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = TranslatorModule.class)
public abstract class AxiomChangeHandlerModule {

  @Binds
  public abstract AxiomChangeHandler
  provideAxiomChangeHandler(AxiomChangeHandlerImpl impl);
}
