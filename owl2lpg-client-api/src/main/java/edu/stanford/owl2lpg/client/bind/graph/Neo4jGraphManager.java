package edu.stanford.owl2lpg.client.bind.graph;

import edu.stanford.bmir.protege.web.server.graph.GraphManager;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.write.project.ProjectUpdater;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jGraphManager implements GraphManager {

  @Nonnull
  private final ProjectUpdater projectUpdater;

  @Inject
  public Neo4jGraphManager(@Nonnull ProjectUpdater projectUpdater) {
    this.projectUpdater = checkNotNull(projectUpdater);
  }

  @Override
  public void setDefaultBranchId(@Nonnull ProjectId projectId, @Nonnull BranchId defaultBranchId) {
    var success = projectUpdater.setDefaultBranchId(projectId, defaultBranchId);
    if (!success) {
      throw new RuntimeException("Unable to se default branch id to project [" + projectId.getId() + "]");
    }
  }

  @Override
  public void setDefaultOntologyDocumentId(@Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @NotNull OntologyDocumentId defaultOntDocId) {
    var success = projectUpdater.setDefaultOntologyDocumentId(projectId, branchId, defaultOntDocId);
    if (!success) {
      throw new RuntimeException("Unable to se default ontology document id to project [" +
          projectId.getId() + "] and branch [" + branchId.getId() + "]");
    }
  }
}
