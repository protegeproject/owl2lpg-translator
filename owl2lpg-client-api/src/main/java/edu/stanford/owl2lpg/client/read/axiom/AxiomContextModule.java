package edu.stanford.owl2lpg.client.read.axiom;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class AxiomContextModule {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontologyDocumentId;

  public AxiomContextModule(@Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontologyDocumentId) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
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
  public OntologyDocumentId provideOntologyDocumentId() {
    return ontologyDocumentId;
  }
}
