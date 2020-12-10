package edu.stanford.owl2lpg.exporter.graphml;

import dagger.Component;
import edu.stanford.owl2lpg.exporter.graphml.writer.GraphmlWriterModule;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;
import edu.stanford.owl2lpg.translator.TranslatorModule;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectSerializerModule;

@Component(modules = {
    TranslatorModule.class,
    GraphmlWriterModule.class,
    OntologyObjectSerializerModule.class})
@TranslationSessionScope
public interface GraphmlExporterComponent {

  OntologyGraphmlExporter getOntologyGraphmlExporter();

  PerAxiomGraphmlExporter getPerAxiomGraphmlExporter();

  OboGraphmlExporter getOboGraphmlExporter();
}
