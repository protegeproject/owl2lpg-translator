package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class QueryBuilder {

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  @Inject
  public QueryBuilder(@Nonnull VariableNameGenerator variableNameGenerator) {
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Nonnull
  public CreateQueryBuilder getCreateQueryBuilder(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId documentId,
                                                  @Nonnull OWLOntologyID ontologyId) {
    return new CreateQueryBuilder(projectId, branchId, documentId, ontologyId, variableNameGenerator);
  }

  @Nonnull
  public DeleteQueryBuilder getDeleteQueryBuilder(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId documentId,
                                                  @Nonnull OWLOntologyID ontologyId) {
    return new DeleteQueryBuilder(projectId, branchId, documentId, ontologyId, variableNameGenerator);
  }
}
