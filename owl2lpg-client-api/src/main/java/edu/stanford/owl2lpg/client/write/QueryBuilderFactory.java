package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLOntologyID;

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
