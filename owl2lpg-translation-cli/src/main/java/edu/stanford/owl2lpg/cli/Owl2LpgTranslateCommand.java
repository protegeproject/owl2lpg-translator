package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.csv.DaggerCsvExporterComponent;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriterModule;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

  private final static int MAX_FILE_SIZE = 600; // MB

  @Parameters(
      index = "0",
      paramLabel = "FILE",
      description = "Input OWL ontology file location",
      type = Path.class)
  Path ontologyFile;

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
        if (isLargeSize(ontologyFile) && isOboFormat(ontologyFile)) {
          System.out.println("Using OBO Stream translator");
          exitCode = translateOboToCsv();
        } else {
          System.out.println("Using OWL translator");
          exitCode = translateOwlToCsv();
        }
        break;
    }
    return exitCode;
  }

  private boolean isLargeSize(Path ontologyFile) {
    return sizeInMB(ontologyFile) > MAX_FILE_SIZE;
  }

  private long sizeInMB(Path filePath) {
    try {
      var sizeInBytes = Files.size(filePath);
      return sizeInBytes / 1024 * 1024;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to get the file size");
    }
  }

  private boolean isOboFormat(Path ontologyFile) {
    return oboExtMatcher.matches(ontologyFile);
  }

  private int translateOboToCsv() {
    int exitCode = 0;
    try {
      var csvWriterModule = new CsvWriterModule(outputDirectoryLocation);
      var exporter = DaggerCsvExporterComponent.builder()
          .csvWriterModule(csvWriterModule)
          .build()
          .getOboCsvExporter();
      exporter.export(ontologyFile, UUID.fromString(projectId),
          UUID.fromString(branchId),
          UUID.fromString(ontDocId), false);
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
      var ontologyManager = OWLManager.createOWLOntologyManager();
      var inputStream = Files.newInputStream(ontologyFile);
      var ontology = ontologyManager.loadOntologyFromOntologyDocument(inputStream);
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
