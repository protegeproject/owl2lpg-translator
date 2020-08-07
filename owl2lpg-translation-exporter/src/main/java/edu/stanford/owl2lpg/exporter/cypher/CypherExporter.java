package edu.stanford.owl2lpg.exporter.cypher;

import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.VersionedOntologyTranslator;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class CypherExporter {

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Path outputFilePath;

  @Nonnull
  private final VersionedOntologyTranslator ontologyTranslator;

  public CypherExporter(@Nonnull OWLOntology ontology,
                        @Nonnull VersionedOntologyTranslator ontologyTranslator, @Nonnull Path outputFilePath) {
    this.ontologyTranslator = checkNotNull(ontologyTranslator);
    this.ontology = checkNotNull(ontology);
    this.outputFilePath = checkNotNull(outputFilePath);
  }

  public void write() {
    var translation = ontologyTranslator.translate(ontology);
    writeTranslation(translation);
  }

  private void writeTranslation(Translation translation) {
    writeNodes(translation.nodes());
    writeEdges(translation.edges());
  }

  void writeNodes(Stream<Node> nodeStream) {
    nodeStream.forEach(node -> {
      var nodeId = node.getNodeId().asString();
      var nodeLabel = node.getLabels().getNeo4jName();
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
      var fromNodeId = edge.getFromNode().getNodeId().asString();
      var toNodeId = edge.getToNode().getNodeId().asString();
      var edgeLabel = edge.getLabel().getNeo4jName();
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
}
