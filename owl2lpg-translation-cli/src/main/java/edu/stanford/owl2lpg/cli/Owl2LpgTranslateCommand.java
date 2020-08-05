package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.csv.CsvWriterModule;
import edu.stanford.owl2lpg.exporter.csv.DaggerOntologyCsvExporterComponent;
import edu.stanford.owl2lpg.exporter.cypher.CypherTranslationExporter;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

@Command(
    name = "translate"
)
// translate ontology.owl  cypher
public class Owl2LpgTranslateCommand implements Callable<Integer> {

  enum Format {cypher, csv}

  @Parameters(
      index = "0",
      paramLabel = "FILE",
      description = "Input OWL ontology file location",
      type = Path.class)
  Path ontologyFileLocation;

  @Option(
      names = {"-f", "--format"},
      description = "Translation format: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})")
  Format format = Format.cypher;

  @Option(
      names = {"-o", "--output"},
      description = "Output directory location",
      type = Path.class)
  Path outputDirectoryLocation;

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Display a help message")
  private boolean helpRequested = false;

  @Override
  public Integer call() throws Exception {
    int exitCode = 0;
    switch (format) {
      case cypher:
        exitCode = translateOntologyToCypher();
        break;
      case csv:
        exitCode = translateOntologyToCsv();
        break;
    }
    return exitCode;
  }

  private int translateOntologyToCypher() {
    int exitCode = 0;
    CypherTranslationExporter exporter = new CypherTranslationExporter();
    try {
      exporter.export(ProjectId.create(),
          BranchId.create(),
          OntologyDocumentId.create(),
          ontologyFileLocation, outputDirectoryLocation);
    } catch (IOException e) {
      e.printStackTrace();
      exitCode = 1;
    }
    return exitCode;
  }

  private int translateOntologyToCsv() {
    int exitCode = 0;
    try {
      var csvWriterModule = new CsvWriterModule(outputDirectoryLocation);
      var exporter = DaggerOntologyCsvExporterComponent.builder()
          .csvWriterModule(csvWriterModule)
          .build()
          .getOntologyCsvExporter();
      var ontologyFile = ontologyFileLocation.toFile();
      var ontologyManager = OWLManager.createOWLOntologyManager();
      var ontology = ontologyManager.loadOntologyFromOntologyDocument(ontologyFile);
      exporter.export(ontology);
    } catch (Exception e) {
      e.printStackTrace();
      exitCode = 1;
    }
    return exitCode;
  }
}
