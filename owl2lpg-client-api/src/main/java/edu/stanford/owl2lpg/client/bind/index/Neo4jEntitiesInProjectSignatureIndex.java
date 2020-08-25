package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.EntitiesInProjectSignatureIndex;
import edu.stanford.owl2lpg.client.read.signature.ProjectSignatureAccessor;
import edu.stanford.owl2lpg.model.ProjectId;
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
  private final ProjectSignatureAccessor projectSignatureAccessor;

  @Inject
  public Neo4jEntitiesInProjectSignatureIndex(@Nonnull ProjectId projectId,
                                              @Nonnull ProjectSignatureAccessor projectSignatureAccessor) {
    this.projectId = checkNotNull(projectId);
    this.projectSignatureAccessor = checkNotNull(projectSignatureAccessor);
  }

  @Override
  public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity) {
    return projectSignatureAccessor.containsEntityInSignature(owlEntity, projectId);
  }
}
