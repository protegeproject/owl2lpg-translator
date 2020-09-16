package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAxiomBySubjectAccessor {

  @Nonnull
  ImmutableSet<OWLSubClassOfAxiom>
  getSubClassOfAxiomsBySubClass(@Nonnull OWLClass subClass,
                                @Nonnull ProjectId projectId,
                                @Nonnull BranchId branchId,
                                @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLSubObjectPropertyOfAxiom>
  getSubObjectPropertyOfAxiomsBySubProperty(@Nonnull OWLObjectProperty subProperty,
                                            @Nonnull ProjectId projectId,
                                            @Nonnull BranchId branchId,
                                            @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLSubDataPropertyOfAxiom>
  getSubDataPropertyOfAxiomsBySubProperty(@Nonnull OWLDataProperty subProperty,
                                          @Nonnull ProjectId projectId,
                                          @Nonnull BranchId branchId,
                                          @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLSubAnnotationPropertyOfAxiom>
  getSubAnnotationPropertyOfAxiomsBySubProperty(@Nonnull OWLAnnotationProperty subProperty,
                                                @Nonnull ProjectId projectId,
                                                @Nonnull BranchId branchId,
                                                @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLSubAnnotationPropertyOfAxiom>
  getSubAnnotationPropertyOfAxiomsBySuperProperty(@Nonnull OWLAnnotationProperty subProperty,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId);
}
