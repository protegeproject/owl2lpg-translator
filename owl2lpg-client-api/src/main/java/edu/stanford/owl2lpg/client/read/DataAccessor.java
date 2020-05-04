package edu.stanford.owl2lpg.client.read;

import edu.stanford.bmir.protege.web.shared.frame.*;
import edu.stanford.owl2lpg.client.DatabaseSession;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataAccessor implements AutoCloseable {

  @Nonnull
  private final DatabaseSession session;

  @Nonnull
  private final AccessorFactory accessorFactory;

  public DataAccessor(@Nonnull DatabaseSession session,
                      @Nonnull AccessorFactory accessorFactory) {
    this.session = checkNotNull(session);
    this.accessorFactory = checkNotNull(accessorFactory);
  }

  public ClassFrame getFrame(AxiomContext context, OWLClass subject) {
    return accessorFactory
        .getFrameAccessor(ClassFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(session);
  }

  public ObjectPropertyFrame getFrame(AxiomContext context, OWLObjectProperty subject) {
    return accessorFactory
        .getFrameAccessor(ObjectPropertyFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(session);
  }

  public DataPropertyFrame getFrame(AxiomContext context, OWLDataProperty subject) {
    return accessorFactory
        .getFrameAccessor(DataPropertyFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(session);
  }

  public AnnotationPropertyFrame getFrame(AxiomContext context, OWLAnnotationProperty subject) {
    return accessorFactory
        .getFrameAccessor(AnnotationPropertyFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(session);
  }

  public NamedIndividualFrame getFrame(AxiomContext context, OWLNamedIndividual subject) {
    return accessorFactory
        .getFrameAccessor(NamedIndividualFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(session);
  }

  @Override
  public void close() throws Exception {
    session.close();
  }
}
