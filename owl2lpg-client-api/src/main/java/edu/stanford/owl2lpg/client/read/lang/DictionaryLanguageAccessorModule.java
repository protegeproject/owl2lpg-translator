package edu.stanford.owl2lpg.client.read.lang;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.lang.impl.DictionaryLanguageAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class DictionaryLanguageAccessorModule {

  @Binds
  public abstract DictionaryLanguageAccessor
  provideDictionaryLanguageAccessor(DictionaryLanguageAccessorImpl impl);
}
