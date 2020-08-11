package edu.stanford.owl2lpg.translator;

import dagger.Component;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    TranslatorModule.class,
    VersioningContextModule.class})
@TranslationSessionScope
public interface TranslatorComponent {

  AxiomTranslator getAxiomTranslator();

  OntologyTranslator getOntologyTranslator();
}
