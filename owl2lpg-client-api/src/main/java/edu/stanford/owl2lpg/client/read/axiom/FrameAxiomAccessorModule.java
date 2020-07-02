package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.DatabaseModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    DatabaseModule.class,
    NodeMapperModule.class})
public abstract class FrameAxiomAccessorModule {

  @Binds
  public abstract FrameAxiomAccessor
  provideFrameAxiomAccessor(FrameAxiomAccessorImpl accessor);
}
