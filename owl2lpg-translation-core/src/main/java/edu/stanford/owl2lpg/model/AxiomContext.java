package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * Represents the versioning provenance for a given OWL axiom.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AxiomContext {

  @Nonnull
  public static AxiomContext create(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId docId) {
    return new AutoValue_AxiomContext(projectId, branchId, docId);
  }

  @Nonnull
  public static AxiomContext create() {
    return create(ProjectId.create(),
        BranchId.create(),
        OntologyDocumentId.create());
  }

  @Nonnull
  public abstract ProjectId getProjectId();

  @Nonnull
  public abstract BranchId getBranchId();

  @Nonnull
  public abstract OntologyDocumentId getOntologyDocumentId();
}
