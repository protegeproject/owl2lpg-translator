package edu.stanford.owl2lpg.exporter.csv;

import dagger.Component;
import edu.stanford.owl2lpg.exporter.csv.writer.WriteOpTrackerModule;
import edu.stanford.owl2lpg.exporter.csv.writer.bulk.BulkCsvWriterModule;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.TranslatorModule;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectDigesterModule;

@Component(modules = {
    TranslatorModule.class,
    BulkCsvWriterModule.class,
    WriteOpTrackerModule.class,
    OntologyObjectDigesterModule.class})
@TranslationSessionScope
public interface BulkCsvExporterComponent {

  OntologyCsvExporter getOntologyCsvExporter();

  PerAxiomCsvExporter getPerAxiomCsvExporter();

  OboCsvExporter getOboCsvExporter();
}
