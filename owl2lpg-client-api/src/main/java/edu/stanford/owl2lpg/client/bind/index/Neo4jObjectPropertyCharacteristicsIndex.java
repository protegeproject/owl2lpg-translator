package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyCharacteristicsIndex;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyCharacteristic;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jObjectPropertyCharacteristicsIndex implements ObjectPropertyCharacteristicsIndex {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyCharacteristicsIndex(@Nonnull ProjectId projectId,
                                                 @Nonnull BranchId branchId,
                                                 @Nonnull OntologyDocumentId ontoDocId,
                                                 @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean hasCharacteristic(@Nonnull OWLObjectProperty owlObjectProperty,
                                   @Nonnull ObjectPropertyCharacteristic objectPropertyCharacteristic,
                                   @Nonnull OWLOntologyID owlOntologyID) {
    switch (objectPropertyCharacteristic) {
      case FUNCTIONAL:
        return characteristicsAxiomAccessor.isFunctional(owlObjectProperty, projectId, branchId, ontoDocId);
      case INVERSE_FUNCTIONAL:
        return characteristicsAxiomAccessor.isInverseFunctional(owlObjectProperty, projectId, branchId, ontoDocId);
      case TRANSITIVE:
        return characteristicsAxiomAccessor.isTransitive(owlObjectProperty, projectId, branchId, ontoDocId);
      case SYMMETRIC:
        return characteristicsAxiomAccessor.isSymmetric(owlObjectProperty, projectId, branchId, ontoDocId);
      case ASYMMETRIC:
        return characteristicsAxiomAccessor.isAsymmetric(owlObjectProperty, projectId, branchId, ontoDocId);
      case REFLEXIVE:
        return characteristicsAxiomAccessor.isReflexive(owlObjectProperty, projectId, branchId, ontoDocId);
      case IRREFLEXIVE:
        return characteristicsAxiomAccessor.isIrreflexive(owlObjectProperty, projectId, branchId, ontoDocId);
      default:
        return false;
    }
  }
}
