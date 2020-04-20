package edu.stanford.owl2lpg.exporter.cypher;

import edu.stanford.owl2lpg.exporter.AbstractTranslationExporter;
import edu.stanford.owl2lpg.translator.TranslatorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

public class CypherTranslationExporter extends AbstractTranslationExporter {

  public void export(@Nonnull File ontologyFile, @Nonnull Writer writer) throws IOException {
    checkNotNull(ontologyFile);
    checkNotNull(writer);
    try {
      var ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile);
      export(ontology, writer);
    } catch (OWLOntologyCreationException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void export(@Nonnull OWLOntology ontology, @Nonnull Writer writer) throws IOException {
    checkNotNull(ontology);
    checkNotNull(writer);
    var exporter = new CypherExporter(
        TranslatorFactory.getOntologyTranslator(),
        ontology,
        writer);
    exporter.write();
    exporter.flush();
  }
}
