package edu.stanford.owl2lpg.client.write;

import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.model.AxiomContext;
import edu.stanford.owl2lpg.translator.VersionedOntologyTranslator;
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
  private final VersionedOntologyTranslator translator;

  public CypherBasedAxiomStorer(@Nonnull Session session,
                                @Nonnull VersionedOntologyTranslator translator) {
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
      stringBuilder.append("MERGE (")
                   .append(printNodeId(node.getNodeId()))
                   .append(printNodeLabel(node.getLabels()))
                   .append(" ")
                   .append(node.getProperties()
                               .printProperties())
                   .append(")");
    } else {
      stringBuilder.append("CREATE (")
                   .append(printNodeId(node.getNodeId()))
                   .append(printNodeLabel(node.getLabels()))
                   .append(" ")
                   .append(node.getProperties()
                               .printProperties())
                   .append(")");
    }
    stringBuilder.append("\n");
  }

  private static void getCypherQuery(Edge edge, StringBuilder stringBuilder) {
    stringBuilder.append("MERGE (")
                 .append(printNodeId(edge.getFromNode()
                                         .getNodeId()))
                 .append(")-[")
                 .append(printEdgeLabel(edge.getLabel()))
                 .append(" ")
                 .append(edge
                                 .getProperties()
                                 .printProperties())
                 .append("]->(")
                 .append(printNodeId(edge.getToNode().getNodeId()))
                 .append(")");
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
    return NodeLabels.PROJECT.equals(nodeLabels);
  }

  private static boolean isBranchNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.BRANCH.equals(nodeLabels);
  }

  private static boolean isOntologyDocumentNode(Node node) {
    var nodeLabels = node.getLabels();
    return NodeLabels.ONTOLOGY_DOCUMENT.equals(nodeLabels);
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
        .map(label -> ":" + label)
        .collect(Collectors.joining(""));
  }

  private static String printEdgeLabel(EdgeLabel edgeLabel) {
    return ":" + edgeLabel.name();
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
