package edu.stanford.owl2lpg.client.write.project;

import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ProjectUpdater {

  @Nonnull
  boolean setDefaultBranchId(@Nonnull ProjectId projectId, @Nonnull BranchId defaultBranchId);

  @Nonnull
  boolean setDefaultOntologyDocumentId(@Nonnull ProjectId projectId, @Nonnull BranchId branchId, @Nonnull OntologyDocumentId defaultOntDocId);
}
