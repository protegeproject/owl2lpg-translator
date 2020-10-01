package edu.stanford.owl2lpg.client.bind.change;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.client.write.handlers.AxiomChangeHandler;
import edu.stanford.owl2lpg.client.write.handlers.AxiomChangeHandlerImpl;
import edu.stanford.owl2lpg.client.write.handlers.OntologyAnnotationChangeHandler;
import edu.stanford.owl2lpg.client.write.handlers.OntologyAnnotationChangeHandlerImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = TranslatorModule.class)
public abstract class OntologyChangeHandlerModule {

  @Binds
  @ProjectSingleton
  public abstract OntologyAnnotationChangeHandler
  provideOntologyAnnotationChangeHandler(OntologyAnnotationChangeHandlerImpl impl);

  @Binds
  @ProjectSingleton
  public abstract AxiomChangeHandler
  provideAxiomChangeHandler(AxiomChangeHandlerImpl impl);
}
