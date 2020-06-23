package edu.stanford.owl2lpg.client.read.frame2;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import org.semanticweb.owlapi.model.OWLDataFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeHandlerModule.class)
public class NodeMapperModule {

  @Provides
  @DatabaseSessionScope
  public OWLDataFactory provideOwlDataFactory() {
    return new OWLDataFactoryImpl();
  }

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
