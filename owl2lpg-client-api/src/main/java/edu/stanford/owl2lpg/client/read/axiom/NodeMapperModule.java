package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.axiom.handlers.NodeHandlerModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeHandlerModule.class)
public class NodeMapperModule {

  @Provides
  @DatabaseSessionScope
  public NodeHandlerRegistry provideNodeHandlerRegistry(NodeHandlerRegistryImpl registry) {
    return registry;
  }

  @Provides
  @DatabaseSessionScope
  public NodeMapper provideNodeMapper(NodeMapperImpl mapper) {
    return mapper;
  }
}
