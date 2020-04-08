package edu.stanford.owl2lpg.exporter.cypher;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
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
import java.util.List;
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
      var nodeId = printNodeId(node.getNodeId());
      var nodeLabel = printNodeLabel(node.getLabels());
      var nodeProperties = printNodeProperties(node.getProperties());
      var s = format("CREATE (%s%s %s)", nodeId, nodeLabel, nodeProperties);
      writeLine(s);
    });
  }

  void writeEdges(Stream<Edge> edgeStream) {
    edgeStream.forEach(edge -> {
      var fromNodeId = printNodeId(edge.getFromNode().getNodeId());
      var toNodeId = printNodeId(edge.getToNode().getNodeId());
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

  private static String printNodeId(int nodeId) {
    return format("NodeId_%d", nodeId);
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
