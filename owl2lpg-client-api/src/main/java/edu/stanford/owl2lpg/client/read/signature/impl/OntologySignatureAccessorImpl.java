package edu.stanford.owl2lpg.client.read.signature.impl;

import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.signature.OntologySignatureAccessor;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologySignatureAccessorImpl implements OntologySignatureAccessor {

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Inject
  public OntologySignatureAccessorImpl(@Nonnull AxiomAccessor axiomAccessor,
                                       @Nonnull EntityAccessor entityAccessor) {
    this.axiomAccessor = checkNotNull(axiomAccessor);
    this.entityAccessor = checkNotNull(entityAccessor);
  }

  @Nonnull
  @Override
  public Set<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                    @Nonnull BranchId branchId,
                                    @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAllAxioms(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public <E extends OWLAxiom> Set<E> getAxiomsByType(@Nonnull AxiomType<E> axiomType,
                                                     @Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAxiomsByType(axiomType, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                       @Nonnull BranchId branchId,
                                       @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getAllEntities(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public <E extends OWLEntity> Set<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByType(entityType, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public Set<OWLEntity> getEntitiesInSignature(@Nonnull IRI iri,
                                               @Nonnull ProjectId projectId,
                                               @Nonnull BranchId branchId,
                                               @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByIri(iri, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean containsAxiomInSignature(@Nonnull OWLAxiom owlAxiom,
                                          @Nonnull ProjectId projectId,
                                          @Nonnull BranchId branchId,
                                          @Nonnull OntologyDocumentId ontoDocId) {
    return getAxiomsByType(owlAxiom.getAxiomType(), projectId, branchId, ontoDocId)
        .stream()
        .anyMatch(owlAxiom::equals);
  }

  @Override
  public boolean containsEntityInSignature(@Nonnull OWLEntity owlEntity,
                                           @Nonnull ProjectId projectId,
                                           @Nonnull BranchId branchId,
                                           @Nonnull OntologyDocumentId ontoDocId) {
    return getEntitiesInSignature(owlEntity.getIRI(), projectId, branchId, ontoDocId)
        .stream()
        .anyMatch(owlEntity::equals);
  }
}
