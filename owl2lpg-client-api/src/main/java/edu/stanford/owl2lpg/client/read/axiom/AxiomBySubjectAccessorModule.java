package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.NodeMapperModule;
import edu.stanford.owl2lpg.client.read.axiom.impl.AxiomBySubjectAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = NodeMapperModule.class)
public abstract class AxiomBySubjectAccessorModule {

  @Binds
  public abstract AxiomBySubjectAccessor
  provideAxiomBySubjectAccessor(AxiomBySubjectAccessorImpl accessor);
}
