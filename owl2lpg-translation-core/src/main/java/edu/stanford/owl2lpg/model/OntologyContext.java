package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class OntologyContext {

  public static OntologyContext get(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontologyDocumentId) {
    return new AutoValue_OntologyContext(projectId, branchId, ontologyDocumentId);
  }

  @Nonnull
  public abstract ProjectId getProjectId();

  @Nonnull
  public abstract BranchId getBranchId();

  @Nonnull
  public abstract OntologyDocumentId getOntologyDocumentId();

  public <O> O accept(OntologyContextVisitor<O> ontologyContextVisitor) {
    return ontologyContextVisitor.visit(this);
  }
}
