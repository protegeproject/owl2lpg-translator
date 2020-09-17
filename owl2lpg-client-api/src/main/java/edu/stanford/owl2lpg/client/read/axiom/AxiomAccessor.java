package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;

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
  default ImmutableSet<OWLSubAnnotationPropertyOfAxiom>
  getSubAnnotationPropertyOfAxiomsBySuperProperty(@Nonnull OWLAnnotationProperty superProperty,
                                                  @Nonnull ProjectId projectId,
                                                  @Nonnull BranchId branchId,
                                                  @Nonnull OntologyDocumentId ontoDocId) {
    return ImmutableSet.of();
  }
}
