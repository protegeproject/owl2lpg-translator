package edu.stanford.owl2lpg.exporter.csv;

import dagger.Component;
import edu.stanford.owl2lpg.exporter.csv.writer.WriteOpTrackerModule;
import edu.stanford.owl2lpg.exporter.csv.writer.apoc.ApocCsvWriterModule;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.TranslatorModule;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectDigesterModule;

@Component(modules = {
    TranslatorModule.class,
    ApocCsvWriterModule.class,
    WriteOpTrackerModule.class,
    OntologyObjectDigesterModule.class})
@TranslationSessionScope
public interface ApocCsvExporterComponent {

  OntologyCsvExporter getOntologyCsvExporter();

  PerAxiomCsvExporter getPerAxiomCsvExporter();

  OboCsvExporter getOboCsvExporter();
}
