package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class QueryBuilderFactory {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Inject
  public QueryBuilderFactory(@Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
  }

  @Nonnull
  public CreateQueryBuilder getCreateQueryBuilder() {
    return new CreateQueryBuilder(projectId, branchId, ontoDocId, new VariableNameGenerator());
  }

  @Nonnull
  public DeleteQueryBuilder getDeleteQueryBuilder() {
    return new DeleteQueryBuilder(projectId, branchId, ontoDocId, new VariableNameGenerator());
  }
}
