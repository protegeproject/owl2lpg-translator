package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface RangeAxiomAccessor {

  @Nonnull
  Set<OWLObjectPropertyRangeAxiom>
  getObjectPropertyRangeAxioms(@Nonnull OWLObjectProperty owlObjectProperty,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLDataPropertyRangeAxiom>
  getDataPropertyRangeAxioms(@Nonnull OWLDataProperty owlDataProperty,
                             @Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  Set<OWLAnnotationPropertyRangeAxiom>
  getAnnotationPropertyRangeAxioms(@Nonnull OWLAnnotationProperty owlAnnotationProperty,
                                   @Nonnull ProjectId projectId,
                                   @Nonnull BranchId branchId,
                                   @Nonnull OntologyDocumentId ontoDocId);
}
