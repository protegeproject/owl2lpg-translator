package edu.stanford.owl2lpg.client.read.individual;

import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NamedIndividualAccessor {

  @Nonnull
  ImmutableSet<OWLNamedIndividual> getAllIndividuals(@Nonnull ProjectId projectId,
                                                     @Nonnull BranchId branchId,
                                                     @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  ImmutableSet<OWLNamedIndividual> getIndividualsByType(@Nonnull OWLClass owlClass,
                                                        @Nonnull ProjectId projectId,
                                                        @Nonnull BranchId branchId,
                                                        @Nonnull OntologyDocumentId ontoDocId);
}
