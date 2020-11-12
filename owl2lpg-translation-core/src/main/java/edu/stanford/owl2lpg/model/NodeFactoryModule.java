package edu.stanford.owl2lpg.model;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.internal.DigestNodeIdProvider;
import edu.stanford.owl2lpg.translator.shared.DigestFunctionModule;

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
@Module(includes = DigestFunctionModule.class)
public abstract class NodeFactoryModule {

  @Binds
  @Named("digest")
  @TranslationSessionScope
  public abstract NodeIdProvider
  provideDigestNodeIdProvider(DigestNodeIdProvider impl);

  @Binds
  @TranslationSessionScope
  public abstract NodeIdMapper provideNodeIdMapper(NodeIdMapperImpl impl);
}
