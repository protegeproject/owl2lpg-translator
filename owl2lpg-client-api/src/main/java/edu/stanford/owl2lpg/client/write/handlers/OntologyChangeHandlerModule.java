package edu.stanford.owl2lpg.client.write.handlers;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = OntologyObjectTranslatorModule.class)
public abstract class OntologyChangeHandlerModule {

  @Binds
  public abstract VariableNameGenerator
  provideVariableNameGenerator(VariableNameGeneratorImpl impl);

  @Binds
  @ProjectSingleton
  public abstract OntologyAnnotationChangeHandler
  provideOntologyAnnotationChangeHandler(OntologyAnnotationChangeHandlerImpl impl);

  @Binds
  @ProjectSingleton
  public abstract AxiomChangeHandler
  provideAxiomChangeHandler(AxiomChangeHandlerImpl impl);
}
