package edu.stanford.owl2lpg.model;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.translator.internal.AugmentedEdgeInclusionCheckerImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class AugmentedEdgeInclusionCheckerModule {

  @Binds
  public abstract AugmentedEdgeInclusionChecker
  provideAugmentedEdgeInclusionChecker(AugmentedEdgeInclusionCheckerImpl impl);
}
