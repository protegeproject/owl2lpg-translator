package edu.stanford.owl2lpg.model;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.internal.DigestEdgeIdProvider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = DigestFunctionModule.class)
public abstract class EdgeFactoryModule {

  @Binds
  @TranslationSessionScope
  public abstract EdgeIdProvider
  provideEdgeIdProvider(DigestEdgeIdProvider impl);
}
