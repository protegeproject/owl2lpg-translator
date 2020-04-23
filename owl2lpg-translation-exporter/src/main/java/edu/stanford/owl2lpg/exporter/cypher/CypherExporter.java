package edu.stanford.owl2lpg.exporter.cypher;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.OntologyTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class CypherExporter {

  @Nonnull
  private final OntologyTranslator ontologyTranslator;

  @Nonnull
  private final OWLOntology ontology;

  @Nonnull
  private final Writer writer;

  CypherExporter(@Nonnull OntologyTranslator ontologyTranslator,
                 @Nonnull OWLOntology ontology,
                 @Nonnull Writer writer) {
    this.ontologyTranslator = checkNotNull(ontologyTranslator);
    this.ontology = checkNotNull(ontology);
    this.writer = checkNotNull(writer);
  }

  public void write() {
    Translation ontologyTranslation = ontologyTranslator.translate(ontology);
    writeNodes(ontologyTranslation.nodes());
    writeEdges(ontologyTranslation.edges());
  }

  void writeNodes(Stream<Node> nodeStream) {
    nodeStream.collect(Collectors.toSet())
        .stream().forEach(node -> {
      var nodeId = node.getNodeId();
      var nodeLabel = printNodeLabel(node.getLabels());
      var nodeProperties = printNodeProperties(node.getProperties());
      var s = format("CREATE (%s%s %s)", nodeId, nodeLabel, nodeProperties);
      writeLine(s);
    });
  }

  void writeEdges(Stream<Edge> edgeStream) {
    edgeStream.forEach(edge -> {
      var fromNodeId = edge.getFromNode().getNodeId();
      var toNodeId = edge.getToNode().getNodeId();
      var edgeLabel = printEdgeLabel(edge.getLabel());
      var edgeProperties = printEdgeProperties(edge.getProperties());
      var s = format("CREATE (%s)-[%s %s]->(%s)", fromNodeId, edgeLabel, edgeProperties, toNodeId);
      writeLine(s);
    });
  }

  public void flush() {
    try {
      writer.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void writeLine(@Nullable String s) {
    if (s == null) {
      return;
    }
    try {
      writer.write(s);
      writer.write(System.getProperty("line.separator"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static String printNodeLabel(List<String> nodeLabels) {
    return nodeLabels.stream()
        .map(label -> format(":%s", label))
        .collect(Collectors.joining(""));
  }

  private static String printNodeProperties(Properties nodeProperties) {
    return printProperties(nodeProperties);
  }

  private static String printProperties(Properties properties) {
    return properties.getMap().keySet().stream()
        .map(key -> {
          var value = properties.get(key);
          if (value instanceof String) {
            return format("%s: \"%s\"", key, escape((String) value));
          } else {
            return format("%s: %s", key, value);
          }
        })
        .collect(Collectors.joining(",", "{", "}"));
  }

  private static String escape(String value) {
    var bytes = value.replaceAll("\n", " ")
        .replaceAll("'", "\\\\'")
        .replaceAll("\"", "\\\\\"")
        .getBytes();
    return new String(bytes, Charsets.UTF_8);
  }

  private static String printEdgeLabel(String edgeLabel) {
    return format(":%s", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, edgeLabel));
  }

  private static String printEdgeProperties(Properties edgeProperties) {
    return printProperties(edgeProperties);
  }
}
