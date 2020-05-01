package edu.stanford.owl2lpg.client.write;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.versioning.translator.AxiomContextTranslator;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomToCypherQuery {

  @Nonnull
  private final AxiomTranslator axiomTranslator;

  @Nonnull
  private final AxiomContextTranslator contextTranslator;

  public AxiomToCypherQuery(@Nonnull AxiomTranslator axiomTranslator,
                            @Nonnull AxiomContextTranslator contextTranslator) {
    this.axiomTranslator = checkNotNull(axiomTranslator);
    this.contextTranslator = checkNotNull(contextTranslator);
  }

  public CypherQuery translate(@Nonnull AxiomBundle bundle) {
    checkNotNull(bundle);
    var sb = new StringBuilder();
    var axiomTranslation = axiomTranslator.translate(bundle.getAxiom());
    translateToCypher(axiomTranslation, sb);
    var contextTranslation = contextTranslator.translate(bundle.getContext());
    translateToCypher(contextTranslation, sb);
    var contextEdge = createContextEdge(contextTranslation, axiomTranslation);
    translateToCypher(contextEdge, sb);
    return CypherQuery.create(sb.toString());
  }

  private static void translateToCypher(Translation translation, StringBuilder stringBuilder) {
    translation.nodes().distinct().forEach(node -> translateToCypher(node, stringBuilder));
    translation.edges().distinct().forEach(edge -> translateToCypher(edge, stringBuilder));
  }

  private Edge createContextEdge(Translation contextTranslation, Translation axiomTranslation) {
    return Edge.create(
        contextTranslation.nodes("OntologyDocument").findFirst().get(),
        axiomTranslation.nodes("Axiom").findFirst().get(),
        EdgeLabels.AXIOM,
        Properties.empty());
  }

  private static void translateToCypher(Node node, StringBuilder stringBuilder) {
    if (isReusableNode(node)) {
      stringBuilder.append(format("MERGE (%s%s %s)",
          printNodeId(node.getNodeId()),
          printNodeLabel(node.getLabels()),
          printNodeProperties(node.getProperties())));
    } else {
      stringBuilder.append(format("CREATE (%s%s %s)",
          printNodeId(node.getNodeId()),
          printNodeLabel(node.getLabels()),
          printNodeProperties(node.getProperties())));
    }
    stringBuilder.append("\n");
  }

  private static void translateToCypher(Edge edge, StringBuilder stringBuilder) {
    stringBuilder.append(format("MERGE (%s)-[%s %s]->(%s)",
        printNodeId(edge.getFromNode().getNodeId()),
        printEdgeLabel(edge.getLabel()),
        printEdgeProperties(edge.getProperties()),
        printNodeId(edge.getToNode().getNodeId())));
    stringBuilder.append("\n");
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
        NodeLabels.DATATYPE.equals(nodeLabels);
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
