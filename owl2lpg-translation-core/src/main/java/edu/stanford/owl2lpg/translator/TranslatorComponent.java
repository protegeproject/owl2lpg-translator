package edu.stanford.owl2lpg.translator;

import dagger.Component;

import javax.inject.Scope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = TranslatorModule.class)
@TranslationSessionScope
public interface TranslatorComponent {

  AxiomTranslator getAxiomTranslator();

  VersionedOntologyTranslator getVersionedOntologyTranslator();
}
