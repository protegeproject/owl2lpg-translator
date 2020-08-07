package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.VersioningContext;
import edu.stanford.owl2lpg.translator.visitors.VersioningContextVisitorImpl;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VersioningContextTranslator {

  @Nonnull
  private final Provider<VersioningContextVisitorImpl> visitor;

  @Inject
  public VersioningContextTranslator(@Nonnull Provider<VersioningContextVisitorImpl> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull VersioningContext versioningContext) {
    checkNotNull(versioningContext);
    return versioningContext.accept(visitor.get());
  }
}
