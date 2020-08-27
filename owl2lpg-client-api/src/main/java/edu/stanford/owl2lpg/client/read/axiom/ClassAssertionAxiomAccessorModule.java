package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.axiom.impl.ClassAssertionAxiomAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeMapperModule.class)
public abstract class ClassAssertionAxiomAccessorModule {

  @Binds
  public abstract ClassAssertionAxiomAccessor
  provideClassAssertionAxiomAccessor(ClassAssertionAxiomAccessorImpl impl);
}
