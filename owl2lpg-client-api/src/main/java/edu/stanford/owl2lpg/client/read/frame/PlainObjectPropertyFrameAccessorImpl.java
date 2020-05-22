package edu.stanford.owl2lpg.client.read.frame;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.bmir.protege.web.shared.frame.PlainObjectPropertyFrame;
import edu.stanford.owl2lpg.model.AxiomContext;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PlainObjectPropertyFrameAccessorImpl implements PlainObjectPropertyFrameAccessor {

  @Nonnull
  private final Session session;

  @Nonnull
  private final ObjectMapper mapper;

  @Inject
  public PlainObjectPropertyFrameAccessorImpl(@Nonnull Session session,
                                              @Nonnull ObjectMapper mapper) {
    this.session = checkNotNull(session);
    this.mapper = checkNotNull(mapper);
  }

  @Override
  public Optional<PlainObjectPropertyFrame> getFrame(@Nonnull AxiomContext context,
                                                     @Nonnull OWLObjectProperty subject) {
    var args = Parameters.forSubject(context, subject);
    return session.readTransaction(tx ->
        tx.run(FrameQueries.PLAIN_OBJECT_PROPERTY_FRAME_QUERY, args)
            .stream()
            .findFirst()
            .map(record -> mapper.convertValue(
                record.asMap().get("result"),
                PlainObjectPropertyFrame.class)));
  }
}
