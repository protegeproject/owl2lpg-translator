package edu.stanford.owl2lpg.client.write.handlers;

import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AxiomChangeHandler {

  void handleAdd(@Nonnull OWLAxiom axiom);

  void handleRemove(@Nonnull OWLAxiom axiom);
}
