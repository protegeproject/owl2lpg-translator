package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.csv.DaggerCsvExporterComponent;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriterModule;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

@Command(
    name = "translate"
)
public class Owl2LpgTranslateCommand implements Callable<Integer> {

  enum Format {csv}

  @Parameters(
      index = "0",
      paramLabel = "FILE",
      description = "Input OWL ontology file location",
      type = Path.class)
  Path ontologyFileLocation;

  @Option(
      names = {"-f", "--format"},
      description = "Translation format: ${COMPLETION-CANDIDATES} (default: ${DEFAULT-VALUE})")
  Format format = Format.csv;

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
      case csv:
        exitCode = translateOntologyToCsv();
        break;
    }
    return exitCode;
  }

  private int translateOntologyToCsv() {
    int exitCode = 0;
    try {
      var csvWriterModule = new CsvWriterModule(outputDirectoryLocation);
      var exporter = DaggerCsvExporterComponent.builder()
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
