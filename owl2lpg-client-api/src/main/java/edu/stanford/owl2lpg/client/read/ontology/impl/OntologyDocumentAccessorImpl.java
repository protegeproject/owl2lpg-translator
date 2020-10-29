package edu.stanford.owl2lpg.client.read.ontology.impl;

import com.google.common.collect.ImmutableSet;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.client.read.annotation.OntologyAnnotationsAccessor;
import edu.stanford.owl2lpg.client.read.axiom.AxiomAccessor;
import edu.stanford.owl2lpg.client.read.entity.EntityAccessor;
import edu.stanford.owl2lpg.client.read.ontology.OntologyDocumentAccessor;
import edu.stanford.owl2lpg.client.read.ontology.ProjectAccessor;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyDocumentAccessorImpl implements OntologyDocumentAccessor {

  @Nonnull
  private final ProjectAccessor projectAccessor;

  @Nonnull
  private final OntologyAnnotationsAccessor ontologyAnnotationsAccessor;

  @Nonnull
  private final AxiomAccessor axiomAccessor;

  @Nonnull
  private final EntityAccessor entityAccessor;

  @Inject
  public OntologyDocumentAccessorImpl(@Nonnull ProjectAccessor projectAccessor,
                                      @Nonnull OntologyAnnotationsAccessor ontologyAnnotationsAccessor,
                                      @Nonnull AxiomAccessor axiomAccessor,
                                      @Nonnull EntityAccessor entityAccessor) {
    this.projectAccessor = checkNotNull(projectAccessor);
    this.ontologyAnnotationsAccessor = checkNotNull(ontologyAnnotationsAccessor);
    this.axiomAccessor = checkNotNull(axiomAccessor);
    this.entityAccessor = checkNotNull(entityAccessor);
  }

  @Nonnull
  @Override
  public OWLOntologyID getOntologyId(@Nonnull ProjectId projectId,
                                     @Nonnull BranchId branchId,
                                     @Nonnull OntologyDocumentId ontoDocId) {
    return projectAccessor.getOntologyDocumentIdMap(projectId, branchId).get(ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAnnotation> getOntologyAnnotations(@Nonnull ProjectId projectId,
                                                            @Nonnull BranchId branchId,
                                                            @Nonnull OntologyDocumentId ontoDocId) {
    return ontologyAnnotationsAccessor.getOntologyAnnotations(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAllAxioms(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public <E extends OWLAxiom> ImmutableSet<E> getAxiomsByType(@Nonnull AxiomType<E> axiomType,
                                                              @Nonnull ProjectId projectId,
                                                              @Nonnull BranchId branchId,
                                                              @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.getAxiomsByType(axiomType, projectId, branchId, ontoDocId);
  }

  @Override
  public boolean containsAxiom(@Nonnull OWLAxiom owlAxiom,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId) {
    return axiomAccessor.containsAxiom(owlAxiom, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntity> getAllEntities(@Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getAllEntities(projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public <E extends OWLEntity> ImmutableSet<E> getEntitiesByType(@Nonnull EntityType<E> entityType,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId,
                                                                 @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByType(entityType, projectId, branchId, ontoDocId);
  }

  @Nonnull
  @Override
  public ImmutableSet<OWLEntity> getEntitiesByIri(@Nonnull IRI iri,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId) {
    return entityAccessor.getEntitiesByIri(iri, projectId, branchId, ontoDocId);
  }
}
