package edu.stanford.owl2lpg.client.bind.change;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.model.AugmentedEdgeInclusionChecker;
import edu.stanford.owl2lpg.model.DigestFunctionModule;
import edu.stanford.owl2lpg.model.EdgeIdProvider;
import edu.stanford.owl2lpg.model.IdFormatChecker;
import edu.stanford.owl2lpg.model.NodeIdMapper;
import edu.stanford.owl2lpg.model.NodeIdMapperImpl;
import edu.stanford.owl2lpg.model.NodeIdProvider;
import edu.stanford.owl2lpg.model.SingleEncounterNodeChecker;
import edu.stanford.owl2lpg.translator.internal.AugmentedEdgeInclusionCheckerImpl;
import edu.stanford.owl2lpg.translator.internal.DigestEdgeIdProvider;
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
public abstract class FactoriesModule {

  @Binds
  @ProjectSingleton
  public abstract NodeIdMapper provideNodeIdMapper(NodeIdMapperImpl impl);

  @Binds
  @Named("number")
  @ProjectSingleton
  public abstract NodeIdProvider
  provideNodeIdProvider(NumberIncrementIdProvider impl);

  @Binds
  @Named("digest")
  @ProjectSingleton
  public abstract NodeIdProvider
  provideDigestNodeIdProvider(DigestNodeIdProvider impl);

  @Binds
  public abstract IdFormatChecker
  provideIdFormatChecker(IdFormatCheckerImpl impl);

  @Binds
  public abstract SingleEncounterNodeChecker
  provideNodeObjectCheckerForSingleEncounter(SingleEncounterNodeCheckerImpl impl);

  @Binds
  @ProjectSingleton
  public abstract EdgeIdProvider
  provideEdgeIdProvider(DigestEdgeIdProvider impl);

  @Binds
  public abstract AugmentedEdgeInclusionChecker
  provideAugmentedEdgeInclusionChecker(AugmentedEdgeInclusionCheckerImpl impl);
}
