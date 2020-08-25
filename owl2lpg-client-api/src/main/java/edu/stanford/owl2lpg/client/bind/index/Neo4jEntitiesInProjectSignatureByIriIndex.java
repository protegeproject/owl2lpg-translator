package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.EntitiesInProjectSignatureByIriIndex;
import edu.stanford.owl2lpg.client.read.signature.ProjectSignatureAccessor;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jEntitiesInProjectSignatureByIriIndex implements EntitiesInProjectSignatureByIriIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final ProjectSignatureAccessor projectSignatureAccessor;

  @Inject
  public Neo4jEntitiesInProjectSignatureByIriIndex(@Nonnull ProjectId projectId,
                                                   @Nonnull ProjectSignatureAccessor projectSignatureAccessor) {
    this.projectId = checkNotNull(projectId);
    this.projectSignatureAccessor = checkNotNull(projectSignatureAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri) {
    return projectSignatureAccessor.getEntitiesInSignature(iri, projectId).stream();
  }
}
