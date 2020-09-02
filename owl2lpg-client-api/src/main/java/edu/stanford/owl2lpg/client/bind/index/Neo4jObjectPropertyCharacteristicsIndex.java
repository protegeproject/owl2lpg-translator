package edu.stanford.owl2lpg.client.bind.index;

import edu.stanford.bmir.protege.web.server.index.ObjectPropertyCharacteristicsIndex;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyCharacteristic;
import edu.stanford.owl2lpg.client.read.axiom.AxiomContext;
import edu.stanford.owl2lpg.client.read.axiom.CharacteristicsAxiomAccessor;
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
  private final AxiomContext axiomContext;

  @Nonnull
  private final CharacteristicsAxiomAccessor characteristicsAxiomAccessor;

  @Inject
  public Neo4jObjectPropertyCharacteristicsIndex(@Nonnull AxiomContext axiomContext,
                                                 @Nonnull CharacteristicsAxiomAccessor characteristicsAxiomAccessor) {
    this.axiomContext = checkNotNull(axiomContext);
    this.characteristicsAxiomAccessor = checkNotNull(characteristicsAxiomAccessor);
  }

  @Override
  public boolean hasCharacteristic(@Nonnull OWLObjectProperty owlObjectProperty,
                                   @Nonnull ObjectPropertyCharacteristic objectPropertyCharacteristic,
                                   @Nonnull OWLOntologyID owlOntologyID) {
    var projectId = axiomContext.getProjectId();
    var branchId = axiomContext.getBranchId();
    var ontoDocId = axiomContext.getOntologyDocumentId();
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
