package edu.stanford.owl2lpg.cli;

import edu.stanford.owl2lpg.exporter.csv.CsvTranslationExporter;
import edu.stanford.owl2lpg.exporter.cypher.CypherTranslationExporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(
    name = "translate"
)
// translate ontology.owl  cypher
public class Owl2LpgTranslateCommand implements Callable<Integer> {

  enum Format {cypher, csv}

  @Parameters(type = File.class, index = "0", paramLabel = "FILE", description = "An OWL ontology file to translate")
  File ontologyFile;

  @Option(names = {"-f"}, description = "Translation format: ${COMPLETION-CANDIDATE} (default: ${DEFAULT-VALUE})")
  Format format = Format.cypher;

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display a help message")
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
      exporter.export(ontologyFile, new PrintWriter(System.out));
    } catch (IOException e) {
      exitCode = 1;
    }
    return exitCode;
  }

  private int translateOntologyToCsv() {
    int exitCode = 0;
    CsvTranslationExporter exporter = new CsvTranslationExporter();
    try {
      exporter.export(ontologyFile, new PrintWriter(System.out));
    } catch (IOException e) {
      exitCode = 1;
    }
    return exitCode;
  }
}
