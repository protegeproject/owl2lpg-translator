package edu.stanford.owl2lpg.exporter.cypher;

import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.OntologyDocumentAxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.OWLAxiom;
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
  private final OntologyDocumentId ontologyDocumentId;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Path outputFilePath;

  @Nonnull
  private final OntologyDocumentAxiomTranslator ontologyTranslator;

  public CypherExporter(@Nonnull OntologyDocumentId ontologyDocumentId,
                        @Nonnull OWLOntology ontology,
                        @Nonnull Path outputFilePath,
                        @Nonnull OntologyDocumentAxiomTranslator ontologyTranslator) {
    this.ontologyTranslator = checkNotNull(ontologyTranslator);
    this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
    this.ontology = checkNotNull(ontology);
    this.outputFilePath = checkNotNull(outputFilePath);
  }

  public void write() {
    ontology.getAxioms()
        .stream()
        .map(this::translateAxiom)
        .forEach(this::writeTranslation);
  }

  private Translation translateAxiom(OWLAxiom axiom) {
    return ontologyTranslator.translate(ontologyDocumentId, axiom);
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
