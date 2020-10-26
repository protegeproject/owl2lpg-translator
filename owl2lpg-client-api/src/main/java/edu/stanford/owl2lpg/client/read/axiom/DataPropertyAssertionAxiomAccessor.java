package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface DataPropertyAssertionAxiomAccessor {

  @Nonnull
  ImmutableSet<OWLDataPropertyAssertionAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                                                           @Nonnull BranchId branchId,
                                                           @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLDataPropertyAssertionAxiom> getAxiomsBySubject(@Nonnull OWLIndividual owlIndividual,
                                                                 @Nonnull ProjectId projectId,
                                                                 @Nonnull BranchId branchId,
                                                                 @Nonnull OntologyDocumentId ontoDocId);
}
