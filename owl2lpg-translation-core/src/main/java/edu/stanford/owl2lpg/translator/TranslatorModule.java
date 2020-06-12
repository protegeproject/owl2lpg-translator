package edu.stanford.owl2lpg.translator;

import com.google.common.hash.Hashing;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class TranslatorModule {

  @Provides
  @Named("counter")
  @TranslationSessionScope
  NodeIdProvider provideNodeIdProvider() {
    return new NumberIncrementIdProvider();
  }

  @Provides
  @Named("digest")
  @TranslationSessionScope
  NodeIdProvider provideDigestNodeIdProvider() {
    return new DigestNodeIdProvider(Hashing.sha256());
  }

  @Provides
  TranslationSessionNodeObjectSingleEncounterChecker provideNodeObjectCheckerForSingleEncounter() {
    return new TranslationSessionNodeObjectSingleEncounterCheckerImpl();
  }

  @Provides
  TranslationSessionNodeObjectMultipleEncountersChecker provideNodeObjectCheckerForMultipleEncounters() {
    return new TranslationSessionNodeObjectMultipleEncountersCheckerImpl();
  }
}
