package edu.stanford.owl2lpg.translator;

import com.google.common.hash.Hashing;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.EdgeIdProvider;
import edu.stanford.owl2lpg.model.IdFormatChecker;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.model.SingleEncounterNodeChecker;
import edu.stanford.owl2lpg.translator.internal.AugmentedEdgeInclusionCheckerImpl;
import edu.stanford.owl2lpg.translator.internal.DigestEdgeIdProvider;
import edu.stanford.owl2lpg.translator.internal.DigestNodeIdProvider;
import edu.stanford.owl2lpg.translator.internal.IdFormatCheckerImpl;
import edu.stanford.owl2lpg.translator.internal.NumberIncrementIdProvider;
import edu.stanford.owl2lpg.translator.internal.SingleEncounterNodeCheckerImpl;
import edu.stanford.owl2lpg.translator.visitors.BuiltInPrefixDeclarationsModule;

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@SuppressWarnings("UnstableApiUsage")
@Module(includes = BuiltInPrefixDeclarationsModule.class)
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
  IdFormatChecker provideIdFormatChecker() {
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
