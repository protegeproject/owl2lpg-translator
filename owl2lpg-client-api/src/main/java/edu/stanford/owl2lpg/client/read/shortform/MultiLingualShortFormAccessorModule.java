package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.shortform.impl.MultiLingualShortFormAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class MultiLingualShortFormAccessorModule {

  @Binds
  public abstract MultiLingualShortFormAccessor
  provideMultiLingualShortFormAccessor(MultiLingualShortFormAccessorImpl impl);
}
