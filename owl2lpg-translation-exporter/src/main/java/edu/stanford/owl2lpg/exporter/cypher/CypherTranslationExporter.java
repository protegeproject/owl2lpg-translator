package edu.stanford.owl2lpg.exporter.cypher;

import edu.stanford.owl2lpg.model.AxiomContext;
import edu.stanford.owl2lpg.translator.DaggerTranslatorComponent;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

public class CypherTranslationExporter {

  public void export(@Nonnull Path ontologyFilePath, @Nonnull Path outputFilePath) throws IOException {
    checkNotNull(ontologyFilePath);
    checkNotNull(outputFilePath);
    try {
      var ontologyFile = ontologyFilePath.toFile();
      var ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontologyFile);
      export(ontology, outputFilePath);
    } catch (OWLOntologyCreationException e) {
      throw new IOException(e);
    }
  }

  public void export(@Nonnull OWLOntology ontology, @Nonnull Path outputFilePath) {
    checkNotNull(ontology);
    checkNotNull(outputFilePath);
    var translator = DaggerTranslatorComponent.create().getVersionedOntologyTranslator();
    var exporter = new CypherExporter(
        translator,
        AxiomContext.create(),
        ontology,
        outputFilePath);
    exporter.write();
  }
}
