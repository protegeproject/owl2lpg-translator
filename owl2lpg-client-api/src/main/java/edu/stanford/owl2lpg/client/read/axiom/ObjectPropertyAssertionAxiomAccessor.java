package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface ObjectPropertyAssertionAxiomAccessor {

  @Nonnull
  ImmutableSet<OWLObjectPropertyAssertionAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                                             @Nonnull BranchId branchId,
                                                             @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLObjectPropertyAssertionAxiom> getAxiomsBySubject(@Nonnull OWLIndividual owlIndividual,
                                                                   @Nonnull ProjectId projectId,
                                                                   @Nonnull BranchId branchId,
                                                                   @Nonnull OntologyDocumentId ontoDocId);
}
