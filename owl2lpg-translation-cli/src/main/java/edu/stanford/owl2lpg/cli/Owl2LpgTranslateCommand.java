package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.cypher.CypherTranslationExporter;

import java.io.IOException;
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
      description = "Output file location",
      type = Path.class)
  Path outputFileLocation;

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
      exporter.export(ontologyFileLocation, outputFileLocation);
    } catch (IOException e) {
      exitCode = 1;
    }
    return exitCode;
  }

  private int translateOntologyToCsv() {
//    int exitCode = 0;
//    OntologyCsvExporter exporter = new OntologyCsvExporter();
//    try {
//      exporter.export(ontologyFile, new PrintWriter(System.out));
//    } catch (IOException e) {
//      exitCode = 1;
//    }
//    return exitCode;
    return 0;
  }
}
