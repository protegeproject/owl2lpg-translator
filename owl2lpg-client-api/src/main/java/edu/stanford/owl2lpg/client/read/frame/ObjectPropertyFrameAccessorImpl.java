package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.inject.Provider;
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

  @Nonnull
  private final Provider<ObjectPropertyFrameRecordHandler> provider;

  public ObjectPropertyFrameAccessorImpl(@Nonnull Session session,
                                         @Nonnull Provider<ObjectPropertyFrameRecordHandler> provider) {
    this.session = checkNotNull(session);
    this.provider = checkNotNull(provider);
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
