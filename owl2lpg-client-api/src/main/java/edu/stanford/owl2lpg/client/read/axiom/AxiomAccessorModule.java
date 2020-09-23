package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.axiom.impl.AxiomAccessorImpl;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializerModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = OntologyObjectSerializerModule.class)
public abstract class AxiomAccessorModule {

  @Binds
  public abstract AxiomAccessor provideAxiomAccessor(AxiomAccessorImpl impl);
}
