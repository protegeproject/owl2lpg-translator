package edu.stanford.owl2lpg.exporter.csv;

import dagger.Component;
import edu.stanford.owl2lpg.exporter.ProjectContextModule;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriterModule;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.TranslatorModule;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializerModule;

@Component(modules = {
    ProjectContextModule.class,
    TranslatorModule.class,
    CsvWriterModule.class,
    OntologyObjectSerializerModule.class})
@TranslationSessionScope
public interface CsvExporterComponent {

  OntologyCsvExporter getOntologyCsvExporter();

  PerAxiomCsvExporter getPerAxiomCsvExporter();
}
