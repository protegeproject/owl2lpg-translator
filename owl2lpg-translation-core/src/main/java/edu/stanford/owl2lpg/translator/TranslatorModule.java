package edu.stanford.owl2lpg.translator;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class TranslatorModule {

  @Provides
  @TranslationSessionScope
  NodeIdProvider provideNodeIdProvider() {
    return new NumberIncrementIdProvider();
  }

  @Provides
  TranslationSessionUniqueNodeChecker provideUniqueNodeChecker(TranslationSessionUniqueNodeCheckerImpl impl) {
    return impl;
  }
}
