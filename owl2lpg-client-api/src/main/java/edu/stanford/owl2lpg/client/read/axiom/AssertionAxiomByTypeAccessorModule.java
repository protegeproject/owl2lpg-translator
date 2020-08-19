package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeMapperModule.class)
public abstract class AssertionAxiomByTypeAccessorModule {

  @Binds
  public abstract AssertionAxiomByTypeAccessor
  provideAssertionAxiomByTypeAccessor(AssertionAxiomByTypeAccessorImpl impl);
}
