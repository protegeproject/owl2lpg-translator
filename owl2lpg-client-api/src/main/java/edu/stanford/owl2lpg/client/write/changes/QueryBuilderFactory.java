package edu.stanford.owl2lpg.client.write.changes;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class QueryBuilderFactory {

  @Inject
  public QueryBuilderFactory() {
  }

  @Nonnull
  public CreateQueryBuilder getCreateQueryBuilder(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId documentId) {
    return new CreateQueryBuilder(projectId, branchId, documentId, new VariableNameGenerator());
  }

  @Nonnull
  public DeleteQueryBuilder getDeleteQueryBuilder(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId documentId) {
    return new DeleteQueryBuilder(projectId, branchId, documentId, new VariableNameGenerator());
  }
}
