package edu.stanford.owl2lpg.exporter.cypher;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class CypherExporter {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final ImmutableSet<OWLAxiom> axioms;

  @Nonnull
  private final Writer writer;

  CypherExporter(@Nonnull AxiomTranslator axiomTranslator,
                 @Nonnull ImmutableSet<OWLAxiom> axioms,
                 @Nonnull Writer writer) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.axioms = checkNotNull(axioms);
    this.writer = checkNotNull(writer);
  }

  public void write() {
    axioms.forEach(axiom -> {
      var translation = axiomTranslator.translate(axiom);
      writeNodes(translation.nodes());
      writeEdges(translation.edges());
    });
  }

  void writeNodes(Stream<Node> nodeStream) {
    nodeStream.forEach(node -> {
      var nodeId = printNodeId(node);
      var nodeLabel = printNodeLabel(node);
      var nodeProperties = printNodeProperties(node);
      var s = format("CREATE (%s%s %s)", nodeId, nodeLabel, nodeProperties);
      writeLine(s);
    });
  }

  void writeEdges(Stream<Edge> edgeStream) {
    edgeStream.forEach(edge -> {
      var fromNodeId = printNodeId(edge.getFromNode());
      var toNodeId = printNodeId(edge.getToNode());
      var edgeLabel = printEdgeLabel(edge);
      var edgeProperties = printEdgeProperties(edge);
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

  private static String printNodeId(Node node) {
    return format("NodeId_%s", node.getNodeId());
  }

  private static String printNodeLabel(Node node) {
    return node.getLabels().stream()
        .map(label -> format(":%s", label))
        .collect(Collectors.joining(""));
  }

  private static String printNodeProperties(Node node) {
    var properties = node.getProperties();
    return printProperties(properties);
  }

  private static String printProperties(Properties properties) {
    return properties.getMap().keySet().stream()
        .map(key -> {
          var value = properties.get(key);
          if (value instanceof String) {
            return format("%s:\"%s\"", key, value);
          } else {
            return format("%s:%s", key, value);
          }
        })
        .collect(Collectors.joining(",", "{", "}"));
  }

  private static String printEdgeLabel(Edge edge) {
    return format(":%s", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, edge.getLabel()));
  }

  private static String printEdgeProperties(Edge edge) {
    var properties = edge.getProperties();
    return printProperties(properties);
  }
}
