package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.EntitiesInProjectSignatureIndex;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jEntitiesInProjectSignatureIndex implements EntitiesInProjectSignatureIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Inject
  public Neo4jEntitiesInProjectSignatureIndex(@Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull ProjectAccessor projectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
  }

  @Override
  public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity) {
    return projectAccessor.getEntitiesByIri(owlEntity.getIRI(), projectId, branchId)
        .anyMatch(owlEntity::equals);
  }
}
