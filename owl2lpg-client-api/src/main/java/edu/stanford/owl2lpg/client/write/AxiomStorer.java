package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomStorer {

  @Nonnull
  boolean add(@Nonnull Collection<OWLAxiom> axioms,
              @Nonnull ProjectId projectId,
              @Nonnull BranchId branchId,
              @Nonnull OntologyDocumentId ontoDocId);
}
