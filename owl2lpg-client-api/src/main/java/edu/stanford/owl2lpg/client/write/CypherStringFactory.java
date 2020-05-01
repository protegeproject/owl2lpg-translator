package edu.stanford.owl2lpg.client.write;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
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

  public static String createNodeQuery(@Nonnull Node node) {
    var sb = new StringBuilder();
    createNodeQuery(node, sb);
    return sb.toString();
  }


  public static String createEdgeQuery(@Nonnull Edge edge) {
    var sb = new StringBuilder();
    createNodeQuery(edge.getFromNode(), sb);
    sb.append("\n");
    createNodeQuery(edge.getToNode(), sb);
    sb.append("\n");
    createEdgeQuery(edge.getFromNode(), edge.getToNode(),
        edge.getLabel(), edge.getProperties(), sb);
    return sb.toString();
  }

  private static void createNodeQuery(Node node, StringBuilder builder) {
    if (isReusable(node)) {
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
  }

  private static void createEdgeQuery(Node fromNode, Node toNode,
                                      String edgeLabel,
                                      Properties edgeProperties,
                                      StringBuilder builder) {
    builder.append(format("MERGE (%s)-[%s %s]->(%s)",
        printNodeId(fromNode.getNodeId()),
        printEdgeLabel(edgeLabel),
        printEdgeProperties(edgeProperties),
        printNodeId(toNode.getNodeId())));
  }

  private static boolean isReusable(Node node) {
    return isEntity(node) ||
        isProject(node) ||
        isBranch(node) ||
        isOntologyDocument(node) ||
        isLiteral(node);
  }

  private static boolean isProject(Node node) {
    var nodeLabels = node.getLabels();
    return edu.stanford.owl2lpg.versioning.translator.NodeLabels.PROJECT.equals(nodeLabels);
  }

  private static boolean isBranch(Node node) {
    var nodeLabels = node.getLabels();
    return edu.stanford.owl2lpg.versioning.translator.NodeLabels.BRANCH.equals(nodeLabels);
  }

  private static boolean isOntologyDocument(Node node) {
    var nodeLabels = node.getLabels();
    return edu.stanford.owl2lpg.versioning.translator.NodeLabels.ONTOLOGY_DOCUMENT.equals(nodeLabels);
  }

  private static boolean isLiteral(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.LITERAL.equals(nodeLabels);
  }

  private static boolean isEntity(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.CLASS.equals(nodeLabels) ||
        NodeLabels.OBJECT_PROPERTY.equals(nodeLabels) ||
        NodeLabels.DATA_PROPERTY.equals(nodeLabels) ||
        NodeLabels.ANNOTATION_PROPERTY.equals(nodeLabels) ||
        NodeLabels.NAMED_INDIVIDUAL.equals(nodeLabels) ||
        NodeLabels.DATA_PROPERTY.equals(nodeLabels);
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
