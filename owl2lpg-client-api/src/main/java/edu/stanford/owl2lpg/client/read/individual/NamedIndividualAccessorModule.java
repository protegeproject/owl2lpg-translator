package edu.stanford.owl2lpg.client.read.individual;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.individual.impl.NamedIndividualAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class NamedIndividualAccessorModule {

  @Binds
  public abstract NamedIndividualAccessor
  provideNamedIndividualAccessor(NamedIndividualAccessorImpl impl);
}
