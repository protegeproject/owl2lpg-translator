package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class NodeHandlerModule {

  @Provides
  @IntoSet
  public NodeHandler<?> provideEntityDeclarationHandler(EntityDeclarationHandler handler) {
    return handler;
  }

  @Provides
  @IntoSet
  public NodeHandler<?> provideSubClassOfHandler(SubClassOfHandler handler) {
    return handler;
  }

  @Provides
  @IntoSet
  public NodeHandler<?> provideOwlClassHandler(ClassHandler handler) {
    return handler;
  }
}
