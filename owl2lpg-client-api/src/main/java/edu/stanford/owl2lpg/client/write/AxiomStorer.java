package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomStorer {

  @Nonnull
  boolean add(@Nonnull AxiomContext context, @Nonnull Collection<OWLAxiom> axioms);
}
