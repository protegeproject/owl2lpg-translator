package edu.stanford.owl2lpg.client.read;

import edu.stanford.bmir.protege.web.shared.frame.*;
import edu.stanford.owl2lpg.client.DatabaseConnection;
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
  private final DatabaseConnection connection;

  @Nonnull
  private final AccessorFactory accessorFactory;

  public DataAccessor(@Nonnull DatabaseConnection connection,
                      @Nonnull AccessorFactory accessorFactory) {
    this.connection = checkNotNull(connection);
    this.accessorFactory = checkNotNull(accessorFactory);
  }

  public ClassFrame getFrame(AxiomContext context, OWLClass subject) {
    return accessorFactory
        .getFrameAccessor(ClassFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(connection);
  }

  public ObjectPropertyFrame getFrame(AxiomContext context, OWLObjectProperty subject) {
    return accessorFactory
        .getFrameAccessor(ObjectPropertyFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(connection);
  }

  public DataPropertyFrame getFrame(AxiomContext context, OWLDataProperty subject) {
    return accessorFactory
        .getFrameAccessor(DataPropertyFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(connection);
  }

  public AnnotationPropertyFrame getFrame(AxiomContext context, OWLAnnotationProperty subject) {
    return accessorFactory
        .getFrameAccessor(AnnotationPropertyFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(connection);
  }

  public NamedIndividualFrame getFrame(AxiomContext context, OWLNamedIndividual subject) {
    return accessorFactory
        .getFrameAccessor(NamedIndividualFrame.class)
        .setParameter(context)
        .setParameter(subject)
        .getFrame(connection);
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }
}
