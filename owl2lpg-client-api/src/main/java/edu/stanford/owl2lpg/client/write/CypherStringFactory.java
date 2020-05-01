package edu.stanford.owl2lpg.client.write;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherStringFactory {

  public static String createCypherStatementFrom(@Nonnull Translation translation) {
    var sb = new StringBuilder();
    createCypherStatementFrom(translation, sb);
    return sb.toString();
  }

  private static void createCypherStatementFrom(@Nonnull Translation translation, StringBuilder sb) {
    translation.nodes()
        .collect(Collectors.toSet())
        .forEach(node -> createCypherStatementFrom(node, sb));
    translation.edges()
        .forEach(edge -> createCypherStatementFrom(edge, sb));
  }

  public static String createCypherStatementFrom(@Nonnull Node node) {
    var sb = new StringBuilder();
    createCypherStatementFrom(node, sb);
    return sb.toString();
  }

  public static String createCypherStatementFrom(@Nonnull Edge edge) {
    var sb = new StringBuilder();
    createCypherStatementFrom(edge.getFromNode(), sb);
    if (!edge.isReflexive()) {
      createCypherStatementFrom(edge.getToNode(), sb);
    }
    createCypherStatementFrom(edge, sb);
    return sb.toString();
  }

  private static void createCypherStatementFrom(Node node, StringBuilder builder) {
    if (isReusableNode(node)) {
      builder.append(format("MERGE (%s%s %s)",
          printNodeId(node.getNodeId()),
          printNodeLabel(node.getLabels()),
          printNodeProperties(node.getProperties())));
    } else {
      builder.append(format("CREATE (%s%s %s)",
          printNodeId(node.getNodeId()),
          printNodeLabel(node.getLabels()),
          printNodeProperties(node.getProperties())));
    }
    builder.append("\n");
  }

  private static void createCypherStatementFrom(Edge edge, StringBuilder builder) {
    builder.append(format("MERGE (%s)-[%s %s]->(%s)",
        printNodeId(edge.getFromNode().getNodeId()),
        printEdgeLabel(edge.getLabel()),
        printEdgeProperties(edge.getProperties()),
        printNodeId(edge.getToNode().getNodeId())));
    builder.append("\n");
  }

  private static boolean isReusableNode(Node node) {
    return isEntityNode(node) ||
        isProjectNode(node) ||
        isBranchNode(node) ||
        isOntologyDocumentNode(node) ||
        isLiteralNode(node) ||
        isIriNode(node);
  }

  private static boolean isEntityNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.CLASS.equals(nodeLabels) ||
        NodeLabels.OBJECT_PROPERTY.equals(nodeLabels) ||
        NodeLabels.DATA_PROPERTY.equals(nodeLabels) ||
        NodeLabels.ANNOTATION_PROPERTY.equals(nodeLabels) ||
        NodeLabels.NAMED_INDIVIDUAL.equals(nodeLabels) ||
        NodeLabels.DATA_PROPERTY.equals(nodeLabels);
  }

  private static boolean isProjectNode(Node node) {
    var nodeLabels = node.getLabels();
    return edu.stanford.owl2lpg.versioning.translator.NodeLabels.PROJECT.equals(nodeLabels);
  }

  private static boolean isBranchNode(Node node) {
    var nodeLabels = node.getLabels();
    return edu.stanford.owl2lpg.versioning.translator.NodeLabels.BRANCH.equals(nodeLabels);
  }

  private static boolean isOntologyDocumentNode(Node node) {
    var nodeLabels = node.getLabels();
    return edu.stanford.owl2lpg.versioning.translator.NodeLabels.ONTOLOGY_DOCUMENT.equals(nodeLabels);
  }

  private static boolean isLiteralNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.LITERAL.equals(nodeLabels);
  }

  private static boolean isIriNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.IRI.equals(nodeLabels);
  }

  private static Object printNodeId(NodeId nodeId) {
    return nodeId.toString().replaceAll("-", "");
  }

  private static String printNodeLabel(List<String> nodeLabels) {
    return nodeLabels.stream()
        .map(label -> format(":%s", label))
        .collect(Collectors.joining(""));
  }

  private static String printEdgeLabel(String edgeLabel) {
    return format(":%s", CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, edgeLabel));
  }

  private static String printNodeProperties(Properties nodeProperties) {
    return printProperties(nodeProperties);
  }

  private static String printEdgeProperties(Properties edgeProperties) {
    return printProperties(edgeProperties);
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
}
