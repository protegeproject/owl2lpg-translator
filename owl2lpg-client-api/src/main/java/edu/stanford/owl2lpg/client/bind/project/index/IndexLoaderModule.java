package edu.stanford.owl2lpg.client.bind.project.index;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class IndexLoaderModule {

  @Binds
  @IntoSet
  public abstract IndexLoader provideDefaultIndexLoader(DefaultIndexLoader impl);

  @Binds
  @IntoSet
  public abstract IndexLoader provideFullTextIndexLoader(FullTextIndexLoader impl);
}
