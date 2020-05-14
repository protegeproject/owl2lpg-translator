package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.PlainClassFrame;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface PlainClassFrameAccessor {

  @Nonnull
  Optional<PlainClassFrame> getFrame(@Nonnull AxiomContext context,
                                     @Nonnull OWLClass subject);
}
