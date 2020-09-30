package edu.stanford.owl2lpg.model;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.translator.internal.DigestNodeIdProvider;
import edu.stanford.owl2lpg.translator.internal.IdFormatCheckerImpl;
import edu.stanford.owl2lpg.translator.internal.NumberIncrementIdProvider;
import edu.stanford.owl2lpg.translator.internal.SingleEncounterNodeCheckerImpl;

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = DigestFunctionModule.class)
public abstract class NodeFactoryModule {

  @Binds
  @Named("number")
  public abstract NodeIdProvider
  provideNodeIdProvider(NumberIncrementIdProvider impl);

  @Binds
  @Named("digest")
  public abstract NodeIdProvider
  provideDigestNodeIdProvider(DigestNodeIdProvider impl);

  @Binds
  public abstract IdFormatChecker
  provideIdFormatChecker(IdFormatCheckerImpl impl);

  @Binds
  public abstract SingleEncounterNodeChecker
  provideNodeObjectCheckerForSingleEncounter(SingleEncounterNodeCheckerImpl impl);
}
