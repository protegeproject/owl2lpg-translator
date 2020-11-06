package edu.stanford.owl2lpg.client.write.changes.handlers;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.client.write.changes.handlers.impl.AxiomChangeHandlerImpl;
import edu.stanford.owl2lpg.client.write.changes.handlers.impl.OntologyAnnotationChangeHandlerImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = OntologyObjectTranslatorModule.class)
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
