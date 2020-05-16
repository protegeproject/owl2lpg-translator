package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.PlainDataPropertyFrame;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface PlainDataPropertyFrameAccessor {

  Optional<PlainDataPropertyFrame> getFrame(@Nonnull AxiomContext context,
                                            @Nonnull OWLDataProperty subject);
}
