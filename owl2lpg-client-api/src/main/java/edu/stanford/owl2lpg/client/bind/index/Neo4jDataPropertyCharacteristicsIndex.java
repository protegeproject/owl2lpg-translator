package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.DataPropertyCharacteristicsIndex;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
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
  private final AxiomContext axiomContext;

  @Nonnull
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jDataPropertyCharacteristicsIndex(@Nonnull AxiomContext axiomContext,
                                               @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean isFunctional(@Nonnull OWLDataProperty owlDataProperty, @Nonnull OWLOntologyID owlOntologyID) {
    return characteristicsAxiomAccessor.isFunctional(owlDataProperty,
        axiomContext.getProjectId(),
        axiomContext.getBranchId(),
        axiomContext.getOntologyDocumentId());
  }
}
