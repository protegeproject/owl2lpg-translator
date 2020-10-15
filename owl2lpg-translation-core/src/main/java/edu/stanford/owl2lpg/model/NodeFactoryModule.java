package edu.stanford.owl2lpg.model;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeIdMapperModule.class)
public abstract class NodeFactoryModule {

  @Binds
  @TranslationSessionScope
  public abstract NodeIdMapper provideNodeIdMapper(NodeIdMapperImpl impl);
}
