package edu.stanford.owl2lpg.exporter.csv;

import dagger.Component;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.TranslatorModule;

@Component(modules = {
    CsvExporterModule.class, TranslatorModule.class
})
@TranslationSessionScope
public interface CsvExporterComponent {

  CsvExporterFactory getCsvExporterFactory();

}
