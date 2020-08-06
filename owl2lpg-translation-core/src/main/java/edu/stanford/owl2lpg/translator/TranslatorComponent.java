package edu.stanford.owl2lpg.translator;

import dagger.Component;
import edu.stanford.owl2lpg.model.NodeIdMapper;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    TranslatorModule.class,
    ProjectContextModule.class})
@TranslationSessionScope
public interface TranslatorComponent {

  NodeIdMapper getNodeIdMapper();

  AxiomTranslator getAxiomTranslator();

  AxiomInProjectTranslator getAxiomInProjectTranslator();

  OntologyDocumentAxiomTranslator getOntologyDocumentAxiomTranslator();

  OntologyInProjectTranslator getOntologyInProjectTranslator();

  SpecificOntologyDocumentAxiomTranslatorFactory getSpecificOntologyDocumentAxiomTranslator();
}
