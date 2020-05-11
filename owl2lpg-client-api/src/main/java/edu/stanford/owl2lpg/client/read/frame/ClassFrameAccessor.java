package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface ClassFrameAccessor {

  @Nonnull
  Optional<ClassFrame> getFrame(@Nonnull AxiomContext context, @Nonnull OWLClass cls);
}
