package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.csv.DaggerCsvExporterComponent;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriterModule;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.UUID;
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
      names = {"-p", "--projectId"},
      description = "Project identifier",
      type = String.class)
  String projectId = UUID.randomUUID().toString();

  @Option(
      names = {"-b", "--branchId"},
      description = "Branch identifier",
      type = String.class)
  String branchId = UUID.randomUUID().toString();

  @Option(
      names = {"-d", "--documentId"},
      description = "Ontology document identifier",
      type = String.class)
  String ontDocId = UUID.randomUUID().toString();

  @Option(
      names = {"-h", "--help"},
      usageHelp = true,
      description = "Display a help message")
  boolean helpRequested = false;

  private final PathMatcher oboExtMatcher = FileSystems.getDefault().getPathMatcher("glob:*.obo");

  @Override
  public Integer call() {
    int exitCode = 0;
    switch (format) {
      case csv:
        var ontologyFile = ontologyFileLocation.getFileName();
        if (oboExtMatcher.matches(ontologyFile)) {
          System.out.println("Using OBO translator");
          exitCode = translateOboToCsv();
        } else {
          System.out.println("Using OWL translator");
          exitCode = translateOwlToCsv();
        }
        break;
    }
    return exitCode;
  }

  private int translateOboToCsv() {
    int exitCode = 0;
    try {
      var csvWriterModule = new CsvWriterModule(outputDirectoryLocation);
      var exporter = DaggerCsvExporterComponent.builder()
          .csvWriterModule(csvWriterModule)
          .build()
          .getOboCsvExporter();
      var ontologyFile = ontologyFileLocation.toFile();
      exporter.export(ontologyFile, UUID.fromString(projectId),
          UUID.fromString(branchId),
          UUID.fromString(ontDocId), true);
    } catch (Exception e) {
      e.printStackTrace();
      exitCode = 1;
    }
    return exitCode;
  }

  private int translateOwlToCsv() {
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
      exporter.export(ontology, UUID.fromString(projectId),
          UUID.fromString(branchId),
          UUID.fromString(ontDocId));
    } catch (Exception e) {
      e.printStackTrace();
      exitCode = 1;
    }
    return exitCode;
  }
}
