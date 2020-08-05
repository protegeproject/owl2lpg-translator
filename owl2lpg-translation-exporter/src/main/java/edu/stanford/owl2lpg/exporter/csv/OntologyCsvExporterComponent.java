package edu.stanford.owl2lpg.exporter.csv;

import dagger.Component;
import edu.stanford.owl2lpg.exporter.ProjectContextModule;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.TranslatorModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Component(modules = {
    ProjectContextModule.class,
    TranslatorModule.class,
    CsvWriterModule.class})
@TranslationSessionScope
public interface OntologyCsvExporterComponent {

  OntologyCsvExporter getOntologyCsvExporter();
}
