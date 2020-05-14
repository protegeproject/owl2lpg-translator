package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyFrameAccessorImpl
    implements ObjectPropertyFrameAccessor, AutoCloseable {

  @Nonnull
  private final Session session;

  public ObjectPropertyFrameAccessorImpl(@Nonnull Session session) {
    this.session = checkNotNull(session);
  }

  @Nonnull
  @Override
  public Optional<ObjectPropertyFrame> getFrame(@Nonnull AxiomContext context,
                                                @Nonnull OWLObjectProperty subject) {
    return Optional.empty();
  }

  @Override
  public void close() throws Exception {
    session.close();
  }
}
