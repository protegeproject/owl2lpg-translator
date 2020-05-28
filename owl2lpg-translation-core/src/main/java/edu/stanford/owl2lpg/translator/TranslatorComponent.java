package edu.stanford.owl2lpg.translator;

import dagger.Component;
import edu.stanford.owl2lpg.translator.visitors.NodeIdMapper;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = TranslatorModule.class)
@TranslationSessionScope
public interface TranslatorComponent {

  NodeIdMapper getNodeIdMapper();

  AxiomTranslator getAxiomTranslator();

  OntologyDocumentAxiomTranslator getOntologyDocumentAxiomTranslator();
}
