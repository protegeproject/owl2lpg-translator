package edu.stanford.owl2lpg.client.write.handlers;

import dagger.Component;
import edu.stanford.bmir.protege.web.shared.inject.ProjectSingleton;
import edu.stanford.owl2lpg.client.DatabaseModule;
import edu.stanford.owl2lpg.translator.OntologyContextModule;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializerModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    DatabaseModule.class,
    OntologyContextModule.class,
    OntologyObjectSerializerModule.class,
    OntologyChangeHandlerModule.class,
    VariableNameGeneratorModule.class
})
@ProjectSingleton
public interface OntologyChangeHandlerComponent {

  AxiomChangeHandler getAxiomChangeHandler();

  OntologyAnnotationChangeHandler getOntologyAnnotationChangeHandler();
}
