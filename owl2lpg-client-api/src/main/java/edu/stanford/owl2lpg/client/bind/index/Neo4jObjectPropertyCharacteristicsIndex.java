package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyCharacteristicsIndex;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyCharacteristic;
import edu.stanford.owl2lpg.client.DocumentIdMap;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
import edu.stanford.owl2lpg.model.BranchId;
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
  private final DocumentIdMap documentIdMap;

  @Nonnull
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyCharacteristicsIndex(@Nonnull ProjectId projectId,
                                                 @Nonnull BranchId branchId,
                                                 @Nonnull DocumentIdMap documentIdMap,
                                                 @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentIdMap = checkNotNull(documentIdMap);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean hasCharacteristic(@Nonnull OWLObjectProperty owlObjectProperty,
                                   @Nonnull ObjectPropertyCharacteristic objectPropertyCharacteristic,
                                   @Nonnull OWLOntologyID ontologyId) {
    var documentId = documentIdMap.get(projectId, ontologyId);
    switch (objectPropertyCharacteristic) {
      case FUNCTIONAL:
        return characteristicsAxiomAccessor.isFunctional(owlObjectProperty, projectId, branchId, documentId);
      case INVERSE_FUNCTIONAL:
        return characteristicsAxiomAccessor.isInverseFunctional(owlObjectProperty, projectId, branchId, documentId);
      case TRANSITIVE:
        return characteristicsAxiomAccessor.isTransitive(owlObjectProperty, projectId, branchId, documentId);
      case SYMMETRIC:
        return characteristicsAxiomAccessor.isSymmetric(owlObjectProperty, projectId, branchId, documentId);
      case ASYMMETRIC:
        return characteristicsAxiomAccessor.isAsymmetric(owlObjectProperty, projectId, branchId, documentId);
      case REFLEXIVE:
        return characteristicsAxiomAccessor.isReflexive(owlObjectProperty, projectId, branchId, documentId);
      case IRREFLEXIVE:
        return characteristicsAxiomAccessor.isIrreflexive(owlObjectProperty, projectId, branchId, documentId);
      default:
        return false;
    }
  }
}
