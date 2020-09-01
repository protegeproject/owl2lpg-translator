package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.axiom.impl.AxiomByTypeAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class AxiomByTypeAccessorModule {

  @Binds
  public abstract AxiomByTypeAccessor provideAxiomByTypeAccessor(AxiomByTypeAccessorImpl impl);
}
