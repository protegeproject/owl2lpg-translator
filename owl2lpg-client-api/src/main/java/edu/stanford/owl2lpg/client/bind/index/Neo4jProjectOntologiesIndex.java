package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ProjectOntologiesIndex;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jProjectOntologiesIndex implements ProjectOntologiesIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Inject
  public Neo4jProjectOntologiesIndex(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull ProjectAccessor projectAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.projectAccessor = checkNotNull(projectAccessor);
  }

  @Nonnull
  @Override
  public Stream<OWLOntologyID> getOntologyIds() {
    return projectAccessor.getOntologyIds(projectId, branchId).stream();
  }
}
