package edu.stanford.owl2lpg.client.bind.change;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.write.changes.handlers.AxiomChangeHandler;
import edu.stanford.owl2lpg.client.write.changes.handlers.OntologyAnnotationChangeHandler;
import edu.stanford.owl2lpg.client.write.changes.handlers.impl.AxiomChangeHandlerImpl;
import edu.stanford.owl2lpg.client.write.changes.handlers.impl.OntologyAnnotationChangeHandlerImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = AxiomTranslatorModule.class)
public abstract class Neo4jOntologyChangeHandlerModule {

  @Binds
  public abstract AxiomChangeHandler
  provideAxiomChangeHandler(AxiomChangeHandlerImpl impl);

  @Binds
  public abstract OntologyAnnotationChangeHandler
  provideOntologyAnnotationChangeHandler(OntologyAnnotationChangeHandlerImpl impl);
}
