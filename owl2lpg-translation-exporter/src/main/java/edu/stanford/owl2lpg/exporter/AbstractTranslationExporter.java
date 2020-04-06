package edu.stanford.owl2lpg.exporter;

import org.semanticweb.owlapi.model.OWLOntology;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractTranslationExporter {

  public abstract void export(OWLOntology ontology, Writer writer) throws IOException;
}
