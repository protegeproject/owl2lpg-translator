package edu.stanford.owl2lpg.client.read.frame2;

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
public abstract class AxiomSubjectAccessorModule {

  @Binds
  public abstract AxiomSubjectAccessor
  provideAxiomSubjectAccessor(AxiomSubjectAccessorImpl accessor);
}
