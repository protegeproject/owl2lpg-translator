package edu.stanford.owl2lpg.translator;

import com.google.common.hash.Hashing;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.EdgeIdProvider;
import edu.stanford.owl2lpg.model.NodeIdProvider;

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@SuppressWarnings("UnstableApiUsage")
@Module
public class TranslatorModule {

  @Provides
  @Named("number")
  @TranslationSessionScope
  NodeIdProvider provideNodeIdProvider() {
    return new NumberIncrementIdProvider();
  }

  @Provides
  @Named("digest")
  @TranslationSessionScope
  NodeIdProvider provideDigestNodeIdProvider() {
    return new DigestNodeIdProvider(Hashing.md5());
  }

  @Provides
  @TranslationSessionScope
  EdgeIdProvider provideEdgeIdProvider() {
    return new DigestEdgeIdProvider(Hashing.md5());
  }

  @Provides
  @TranslationSessionScope
  IdFormatChecker provideNodeObjectCheckerForMultipleEncounters() {
    return new IdFormatCheckerImpl();
  }

  @Provides
  @TranslationSessionScope
  SingleEncounterNodeChecker provideNodeObjectCheckerForSingleEncounter() {
    return new SingleEncounterNodeCheckerImpl();
  }

  @Provides
  @TranslationSessionScope
  AugmentedEdgeInclusionChecker provideAugmentedEdgeInclusionChecker() {
    return new AugmentedEdgeInclusionCheckerImpl();
  }
}
