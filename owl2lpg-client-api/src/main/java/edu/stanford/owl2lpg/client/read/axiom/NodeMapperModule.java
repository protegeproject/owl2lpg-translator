package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.client.read.axiom.handlers.NodeHandlerModule;
import edu.stanford.owl2lpg.client.read.axiom.impl.NodeHandlerRegistryImpl;
import edu.stanford.owl2lpg.client.read.axiom.impl.NodeMapperImpl;

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
