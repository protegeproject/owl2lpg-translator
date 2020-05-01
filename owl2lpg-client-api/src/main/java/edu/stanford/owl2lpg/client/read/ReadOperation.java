package edu.stanford.owl2lpg.client.read;

import edu.stanford.bmir.protege.web.shared.frame.*;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ReadOperation {

  @Nonnull
  private final Database database;

  @Nonnull
  private final ResultMapperFactory resultMapperFactory;

  public ReadOperation(@Nonnull Database database,
                       @Nonnull ResultMapperFactory resultMapperFactory) {
    this.database = checkNotNull(database);
    this.resultMapperFactory = checkNotNull(resultMapperFactory);
  }

  public ClassFrame getFrame(AxiomContext context, OWLClass subject) {
//    var result = database.execute("");
//    var resultClassFrame = resultMapperFactory.getResultClassFrame(result);
//    return resultClassFrame.getClassFrame();
    return null;
  }

  public ObjectPropertyFrame getFrame(AxiomContext context, OWLObjectProperty subject) {
    return null;
  }

  public DataPropertyFrame getFrame(AxiomContext context, OWLDataProperty subject) {
    return null;
  }

  public AnnotationPropertyFrame getFrame(AxiomContext context, OWLAnnotationProperty subject) {
    return null;
  }

  public NamedIndividualFrame getFrame(AxiomContext context, OWLNamedIndividual subject) {
    return null;
  }

  public void close() throws Exception {
    database.close();
  }
}
