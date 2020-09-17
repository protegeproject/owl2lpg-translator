package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
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
  ImmutableSet<OWLAxiom> getAxiomsBySubject(@Nonnull OWLEntity entity,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLAxiom> getAxiomsBySubjects(@Nonnull Collection<OWLEntity> entities,
                                             @Nonnull ProjectId projectId,
                                             @Nonnull BranchId branchId,
                                             @Nonnull OntologyDocumentId ontoDocId);

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
