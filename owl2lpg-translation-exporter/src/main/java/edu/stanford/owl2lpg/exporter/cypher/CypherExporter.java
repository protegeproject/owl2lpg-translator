package edu.stanford.owl2lpg.exporter.cypher;

import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.AxiomContext;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.VersionedOntologyTranslator;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class CypherExporter {

  @Nonnull
  private final VersionedOntologyTranslator ontologyTranslator;

  @Nonnull
  private final AxiomContext axiomContext;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Path outputFilePath;

  CypherExporter(@Nonnull VersionedOntologyTranslator ontologyTranslator,
                 @Nonnull AxiomContext axiomContext,
                 @Nonnull OWLOntology ontology,
                 @Nonnull Path outputFilePath) {
    this.ontologyTranslator = checkNotNull(ontologyTranslator);
    this.axiomContext = axiomContext;
    this.ontology = checkNotNull(ontology);
    this.outputFilePath = outputFilePath;
  }

  public void write() {
    ontology.getAxioms()
        .stream()
        .map(axiom -> ontologyTranslator.translate(axiomContext, axiom))
        .forEach(this::writeTranslation);
  }

  private void writeTranslation(Translation translation) {
    writeNodes(translation.nodes());
    writeEdges(translation.edges());
  }

  void writeNodes(Stream<Node> nodeStream) {
    nodeStream.forEach(node -> {
      var nodeId = node.getNodeId();
      var nodeLabel = printNodeLabel(node.getLabels());
      var nodeProperties = node.getProperties().printProperties();
      var s = "MERGE (" +
              nodeId +
              nodeLabel +
              " " +
              nodeProperties +
              ")";
      writeLine(s);
    });
  }

  void writeEdges(Stream<Edge> edgeStream) {
    edgeStream.forEach(edge -> {
      var fromNodeId = edge.getFromNode().getNodeId();
      var toNodeId = edge.getToNode().getNodeId();
      var edgeLabel = printEdgeLabel(edge.getLabel());
      var edgeProperties = edge.getProperties().printProperties();
      var s = "MERGE (" +
              fromNodeId +
              ")-[" +
              edgeLabel +
              " " +
              edgeProperties +
              "]->(" +
              toNodeId +
              ")";
      writeLine(s);
    });
  }

  private void writeLine(@Nullable String str) {
    if (str == null) {
      return;
    }
    try {
      var strWithNewline = str + "\n";
      Files.writeString(outputFilePath,
          strWithNewline,
          Charsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String printNodeLabel(List<String> nodeLabels) {
    return nodeLabels.stream()
        .map(label -> ":" + label)
        .collect(Collectors.joining(""));
  }

  private static String printEdgeLabel(EdgeLabel edgeLabel) {
    return ":" + edgeLabel.name();
  }
}
