package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.AnnotationPropertyFrame;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface AnnotationPropertyFrameAccessor {

  @Nonnull
  Optional<AnnotationPropertyFrame> getFrame(@Nonnull AxiomContext context,
                                             @Nonnull OWLAnnotationProperty subject);
}
