package edu.stanford.owl2lpg.client.write.handlers;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomChangeHandler<T extends OWLAxiom> {

  void handleAdd(@Nonnull ProjectId projectId,
                 @Nonnull BranchId branchId,
                 @Nonnull OntologyDocumentId ontoDocId,
                 @Nonnull T axiom);

  void handleRemove(@Nonnull ProjectId projectId,
                    @Nonnull BranchId branchId,
                    @Nonnull OntologyDocumentId ontoDocId,
                    @Nonnull T axiom);
}
