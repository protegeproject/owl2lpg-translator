package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.DataPropertyCharacteristicsIndex;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jDataPropertyCharacteristicsIndex implements DataPropertyCharacteristicsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jDataPropertyCharacteristicsIndex(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean isFunctional(@Nonnull OWLDataProperty owlDataProperty, @Nonnull OntologyDocumentId ontDocId) {
    return characteristicsAxiomAccessor.isFunctional(owlDataProperty, projectId, branchId, ontDocId);
  }
}
