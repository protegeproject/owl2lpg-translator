package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class MultiLingualDictionaryContextModule {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final FullTextIndexName annotationValueFullTextIndexName;

  public MultiLingualDictionaryContextModule(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull FullTextIndexName annotationValueFullTextIndexName) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.annotationValueFullTextIndexName = checkNotNull(annotationValueFullTextIndexName);
  }

  @Provides
  public ProjectId provideProjectId() {
    return projectId;
  }

  @Provides
  public BranchId provideBranchId() {
    return branchId;
  }

  @Provides
  public FullTextIndexName provideAnnotationValueFullTextIndexName() {
    return annotationValueFullTextIndexName;
  }
}
