package edu.stanford.owl2lpg.translator.util;

import org.semanticweb.owlapi.model.OWLObject;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface OntologyObjectSerializer {

  @Nonnull
  ByteArray getByteArray(@Nonnull OWLObject owlObject);
}
