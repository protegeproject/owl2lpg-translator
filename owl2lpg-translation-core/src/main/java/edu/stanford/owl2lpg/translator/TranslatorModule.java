package edu.stanford.owl2lpg.translator;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;

import javax.inject.Named;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    try {
      return new DigestNodeIdProvider(MessageDigest.getInstance("SHA-256"));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  TranslationSessionNodeObjectSingleEncounterChecker provideNodeObjectCheckerForSingleEncounter(
      TranslationSessionNodeObjectSingleEncounterCheckerImpl impl) {
    return impl;
  }

  @Provides
  TranslationSessionNodeObjectMultipleEncountersChecker provideNodeObjectCheckerForMultipleEncounters(
      TranslationSessionNodeObjectMultipleEncountersCheckerImpl impl) {
    return impl;
  }
}
