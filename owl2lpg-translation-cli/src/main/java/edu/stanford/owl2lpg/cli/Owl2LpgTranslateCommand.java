package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.csv.DaggerOntologyCsvExporterComponent;
import edu.stanford.owl2lpg.exporter.csv.OntologyModule;
import edu.stanford.owl2lpg.exporter.cypher.CypherTranslationExporter;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

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
      exporter.export(ontologyFileLocation, outputDirectoryLocation);
    } catch (IOException e) {
      e.printStackTrace();
      exitCode = 1;
    }
    return exitCode;
  }

  private int translateOntologyToCsv() {
    int exitCode = 0;
    try {
      Files.createDirectories(outputDirectoryLocation);
      var nodesCsvPath = outputDirectoryLocation.resolve("nodes.csv");
      var relationshipsCsvPath = outputDirectoryLocation.resolve("relationships.csv");
      try (var nodesCsvWriter = createWriter(nodesCsvPath);
           var relsCsvWriter = createWriter(relationshipsCsvPath)) {
        var ontologyModule = new OntologyModule(ontologyFileLocation.toFile());
        var exporter = DaggerOntologyCsvExporterComponent.builder()
            .ontologyModule(ontologyModule)
            .build()
            .getOntologyCsvExporter();
        exporter.export(nodesCsvWriter, relsCsvWriter);
      }
    } catch (IOException e) {
      e.printStackTrace();
      exitCode = 1;
    }
    return exitCode;
  }

  private static Writer createWriter(@Nonnull Path path) throws IOException {
    return new BufferedWriter(new FileWriter(path.toFile()));
  }
}
