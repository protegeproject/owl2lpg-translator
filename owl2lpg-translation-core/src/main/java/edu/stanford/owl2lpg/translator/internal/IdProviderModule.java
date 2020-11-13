package edu.stanford.owl2lpg.translator.internal;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.model.EdgeIdProvider;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.translator.shared.HashFunctionModule;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializerModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    OntologyObjectSerializerModule.class,
    HashFunctionModule.class})
public abstract class IdProviderModule {

  @Binds
  public abstract NodeIdProvider provideNodeIdProvider(HashNodeIdProvider impl);

  @Binds
  public abstract EdgeIdProvider provideEdgeIdProvider(HashEdgeIdProvider impl);
}
