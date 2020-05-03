package edu.stanford.owl2lpg.client.read;

import com.google.auto.value.AutoValue;
import edu.stanford.bmir.protege.web.shared.frame.*;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DataAccessor implements AutoCloseable {

  public static DataAccessor create(@Nonnull Database database,
                                    @Nonnull Session session,
                                    @Nonnull DataAccessorFactory dataAccessorFactory) {
    return new AutoValue_DataAccessor(database, session, dataAccessorFactory);
  }

  public ClassFrame getFrame(AxiomContext context, OWLClass subject) {
    return getDataAccessorFactory()
        .getFrameAccessor(ClassFrame.class, getDatabase(), getSession())
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public ObjectPropertyFrame getFrame(AxiomContext context, OWLObjectProperty subject) {
    return getDataAccessorFactory()
        .getFrameAccessor(ObjectPropertyFrame.class, getDatabase(), getSession())
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public DataPropertyFrame getFrame(AxiomContext context, OWLDataProperty subject) {
    return getDataAccessorFactory()
        .getFrameAccessor(DataPropertyFrame.class, getDatabase(), getSession())
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public AnnotationPropertyFrame getFrame(AxiomContext context, OWLAnnotationProperty subject) {
    return getDataAccessorFactory()
        .getFrameAccessor(AnnotationPropertyFrame.class, getDatabase(), getSession())
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  public NamedIndividualFrame getFrame(AxiomContext context, OWLNamedIndividual subject) {
    return getDataAccessorFactory()
        .getFrameAccessor(NamedIndividualFrame.class, getDatabase(), getSession())
        .setParameter(context)
        .setParameter(subject)
        .getFrame();
  }

  @Override
  public void close() throws Exception {
    getSession().close();
  }

  public abstract Database getDatabase();

  protected abstract Session getSession();

  protected abstract DataAccessorFactory getDataAccessorFactory();
}
