package edu.stanford.owl2lpg.client;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class OntologyProjectModule {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId = BranchId.create();

  public OntologyProjectModule(@Nonnull ProjectId projectId) {
    this.projectId = checkNotNull(projectId);
  }

  @Provides
  public ProjectId provideProjectId() {
    return projectId;
  }

  @Provides
  public BranchId provideBranchId() {
    return branchId;
  }
}
