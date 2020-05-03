package edu.stanford.owl2lpg.client.read;

import edu.stanford.bmir.protege.web.shared.frame.*;
import edu.stanford.owl2lpg.client.Database;
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
  private final Database database;

  @Nonnull
  private final DatabaseConnection connection;

  @Nonnull
  private final AccessorFactory accessorFactory;

  public DataAccessor(@Nonnull Database database,
                      @Nonnull DatabaseConnection connection,
                      @Nonnull AccessorFactory accessorFactory) {
    this.database = checkNotNull(database);
    this.connection = checkNotNull(connection);
    this.accessorFactory = checkNotNull(accessorFactory);
  }

  public ClassFrame getFrame(AxiomContext context, OWLClass subject) {
    return accessorFactory
        .getFrameAccessor(ClassFrame.class, database, connection)
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public ObjectPropertyFrame getFrame(AxiomContext context, OWLObjectProperty subject) {
    return accessorFactory
        .getFrameAccessor(ObjectPropertyFrame.class, database, connection)
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public DataPropertyFrame getFrame(AxiomContext context, OWLDataProperty subject) {
    return accessorFactory
        .getFrameAccessor(DataPropertyFrame.class, database, connection)
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public AnnotationPropertyFrame getFrame(AxiomContext context, OWLAnnotationProperty subject) {
    return accessorFactory
        .getFrameAccessor(AnnotationPropertyFrame.class, database, connection)
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public NamedIndividualFrame getFrame(AxiomContext context, OWLNamedIndividual subject) {
    return accessorFactory
        .getFrameAccessor(NamedIndividualFrame.class, database, connection)
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }
}
