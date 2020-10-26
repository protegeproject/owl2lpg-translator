package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomAccessor {

  @Nonnull
  ImmutableSet<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                      @Nonnull BranchId branchId,
                                      @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  <T extends OWLAxiom> ImmutableSet<T> getAxiomsByType(@Nonnull AxiomType<T> axiomType,
                                                       @Nonnull ProjectId projectId,
                                                       @Nonnull BranchId branchId,
                                                       @Nonnull OntologyDocumentId ontoDocId);

  boolean containsAxiom(@Nonnull OWLAxiom owlAxiom,
                        @Nonnull ProjectId projectId,
                        @Nonnull BranchId branchId,
                        @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySignature(@Nonnull OWLEntity entitySignature,
                                              @Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId);
  
  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLClass subject,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLDataProperty subject,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLObjectProperty subject,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLAnnotationProperty subject,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLNamedIndividual subject,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLDatatype subject,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  default ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLEntity subject,
                                                    @Nonnull ProjectId projectId,
                                                    @Nonnull BranchId branchId,
                                                    @Nonnull OntologyDocumentId ontoDocId) {
    if (subject.isOWLClass()) {
      return getAxiomsBySubject(subject.asOWLClass(), projectId, branchId, ontoDocId);
    } else if (subject.isOWLDataProperty()) {
      return getAxiomsBySubject(subject.asOWLDataProperty(), projectId, branchId, ontoDocId);
    } else if (subject.isOWLObjectProperty()) {
      return getAxiomsBySubject(subject.asOWLObjectProperty(), projectId, branchId, ontoDocId);
    } else if (subject.isOWLAnnotationProperty()) {
      return getAxiomsBySubject(subject.asOWLAnnotationProperty(), projectId, branchId, ontoDocId);
    } else if (subject.isOWLNamedIndividual()) {
      return getAxiomsBySubject(subject.asOWLNamedIndividual(), projectId, branchId, ontoDocId);
    } else { // must be a datatype
      return getAxiomsBySubject(subject.asOWLDatatype(), projectId, branchId, ontoDocId);
    }
  }

  @Nonnull
  default ImmutableSet<OWLAxiom> getAxiomsBySubjects(@Nonnull Collection<OWLEntity> entities,
                                                     @Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId) {
    return entities.stream()
        .flatMap(entity -> getAxiomsBySubject(entity, projectId, branchId, ontoDocId).stream())
        .collect(ImmutableSet.toImmutableSet());
  }

  @Nonnull
  ImmutableSet<OWLAnnotationAxiom> getAnnotationAxioms(@Nonnull IRI iri,
                                                       @Nonnull ProjectId projectId,
                                                       @Nonnull BranchId branchId,
                                                       @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  default ImmutableSet<OWLSubAnnotationPropertyOfAxiom>
  getSubAnnotationPropertyOfAxiomsBySuperProperty(@Nonnull OWLAnnotationProperty superProperty,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId) {
    return ImmutableSet.of();
  }
}
