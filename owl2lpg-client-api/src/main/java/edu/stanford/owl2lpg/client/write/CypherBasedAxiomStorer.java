package edu.stanford.owl2lpg.client.write;

import com.google.common.base.CaseFormat;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx;
import org.neo4j.driver.Session;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CypherBasedAxiomStorer implements AxiomStorer, AutoCloseable {

  @Nonnull
  private final Session session;

  @Nonnull
  private final AxiomTranslatorEx translator;

  public CypherBasedAxiomStorer(@Nonnull Session session,
                                @Nonnull AxiomTranslatorEx translator) {
    this.session = checkNotNull(session);
    this.translator = checkNotNull(translator);
  }

  @Nonnull
  @Override
  public boolean add(@Nonnull AxiomContext context, @Nonnull Collection<OWLAxiom> axioms) {
    return axioms
        .stream()
        .map(axiom -> translator.translate(context, axiom))
        .map(this::getCypherQuery)
        .map(query ->
            session.writeTransaction(tx -> {
              tx.run(query);
              return true;
            }))
        .reduce(Boolean::logicalAnd)
        .orElse(false);
  }

  @Override
  public void close() throws Exception {
    session.close();
  }

  private String getCypherQuery(Translation translation) {
    var sb = new StringBuilder();
    getCypherQuery(translation, sb);
    return sb.toString();
  }

  private static void getCypherQuery(Translation translation, StringBuilder stringBuilder) {
    translation.nodes().distinct().forEach(node -> getCypherQuery(node, stringBuilder));
    translation.edges().distinct().forEach(edge -> getCypherQuery(edge, stringBuilder));
  }

  private static void getCypherQuery(Node node, StringBuilder stringBuilder) {
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

  private static void getCypherQuery(Edge edge, StringBuilder stringBuilder) {
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
        isIriNode(node) ||
        isLanguageTagNode(node);
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
    return AxiomTranslatorEx.NodeLabels.PROJECT.equals(nodeLabels);
  }

  private static boolean isBranchNode(Node node) {
    var nodeLabels = node.getLabels();
    return AxiomTranslatorEx.NodeLabels.BRANCH.equals(nodeLabels);
  }

  private static boolean isOntologyDocumentNode(Node node) {
    var nodeLabels = node.getLabels();
    return AxiomTranslatorEx.NodeLabels.ONTOLOGY_DOCUMENT.equals(nodeLabels);
  }

  private static boolean isLiteralNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.LITERAL.equals(nodeLabels);
  }

  private static boolean isIriNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.IRI.equals(nodeLabels);
  }

  private static boolean isLanguageTagNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.LANGUAGE_TAG.equals(nodeLabels);
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
    String escaped = value;
    escaped = escaped.replace("\\", "\\\\");
    escaped = escaped.replace("\"", "\\\"");
    escaped = escaped.replace("\b", "\\b");
    escaped = escaped.replace("\f", "\\f");
    escaped = escaped.replace("\n", "\\n");
    escaped = escaped.replace("\r", "\\r");
    escaped = escaped.replace("\t", "\\t");
    return escaped;
  }
}
