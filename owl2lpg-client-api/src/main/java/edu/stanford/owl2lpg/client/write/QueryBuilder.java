package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class QueryBuilder {

  @Inject
  public QueryBuilder() {
  }

  @Nonnull
  public CreateQueryBuilder getCreateQueryBuilder(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId documentId,
                                                  @Nonnull OWLOntologyID ontologyId) {
    return new CreateQueryBuilder(projectId, branchId, documentId, ontologyId, new VariableNameGenerator());
  }

  @Nonnull
  public DeleteQueryBuilder getDeleteQueryBuilder(@Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId documentId,
                                                  @Nonnull OWLOntologyID ontologyId) {
    return new DeleteQueryBuilder(projectId, branchId, documentId, ontologyId, new VariableNameGenerator());
  }
}
