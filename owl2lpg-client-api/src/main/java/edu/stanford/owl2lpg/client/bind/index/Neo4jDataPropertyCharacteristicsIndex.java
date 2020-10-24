package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.DataPropertyCharacteristicsIndex;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyID;

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
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jDataPropertyCharacteristicsIndex(@Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull DocumentIdMap documentIdMap,
                                               @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean isFunctional(@Nonnull OWLDataProperty owlDataProperty, @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    return characteristicsAxiomAccessor.isFunctional(owlDataProperty, projectId, branchId, documentId);
  }
}
