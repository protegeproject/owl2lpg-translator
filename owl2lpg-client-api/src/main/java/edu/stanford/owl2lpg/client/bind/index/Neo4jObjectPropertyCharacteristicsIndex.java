package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyCharacteristicsIndex;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyCharacteristic;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import org.semanticweb.owlapi.model.OWLObjectProperty;

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
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyCharacteristicsIndex(@Nonnull ProjectId projectId,
                                                 @Nonnull BranchId branchId,
                                                 @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean hasCharacteristic(@Nonnull OWLObjectProperty owlObjectProperty,
                                   @Nonnull ObjectPropertyCharacteristic objectPropertyCharacteristic,
                                   @Nonnull OntologyDocumentId ontDocId) {
    switch (objectPropertyCharacteristic) {
      case FUNCTIONAL:
        return characteristicsAxiomAccessor.isFunctional(owlObjectProperty, projectId, branchId, ontDocId);
      case INVERSE_FUNCTIONAL:
        return characteristicsAxiomAccessor.isInverseFunctional(owlObjectProperty, projectId, branchId, ontDocId);
      case TRANSITIVE:
        return characteristicsAxiomAccessor.isTransitive(owlObjectProperty, projectId, branchId, ontDocId);
      case SYMMETRIC:
        return characteristicsAxiomAccessor.isSymmetric(owlObjectProperty, projectId, branchId, ontDocId);
      case ASYMMETRIC:
        return characteristicsAxiomAccessor.isAsymmetric(owlObjectProperty, projectId, branchId, ontDocId);
      case REFLEXIVE:
        return characteristicsAxiomAccessor.isReflexive(owlObjectProperty, projectId, branchId, ontDocId);
      case IRREFLEXIVE:
        return characteristicsAxiomAccessor.isIrreflexive(owlObjectProperty, projectId, branchId, ontDocId);
      default:
        return false;
    }
  }
}
