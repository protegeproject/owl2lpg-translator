package edu.stanford.owl2lpg.client.read;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.client.read.handlers.NodeHandlerModule;
import edu.stanford.owl2lpg.client.read.impl.NodeHandlerRegistryImpl;
import edu.stanford.owl2lpg.client.read.impl.NodeMapperImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeHandlerModule.class)
public class NodeMapperModule {

  @Provides
  public NodeHandlerRegistry provideNodeHandlerRegistry(NodeHandlerRegistryImpl registry) {
    return registry;
  }

  @Provides
  public NodeMapper provideNodeMapper(NodeMapperImpl mapper) {
    return mapper;
  }
}
