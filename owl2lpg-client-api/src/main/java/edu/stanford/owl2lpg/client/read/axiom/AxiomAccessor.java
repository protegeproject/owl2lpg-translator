package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomAccessor {

  @Nonnull
  Set<OWLAxiom> getAllAxioms(@Nonnull ProjectId projectId,
                             @Nonnull BranchId branchId,
                             @Nonnull OntologyDocumentId ontoDocId);

  @Nonnull
  <T extends OWLAxiom> Set<T> getAxiomsByType(@Nonnull AxiomType<T> axiomType,
                                              @Nonnull ProjectId projectId,
                                              @Nonnull BranchId branchId,
                                              @Nonnull OntologyDocumentId ontoDocId);
}
