package edu.stanford.owl2lpg.client.write.handlers;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM_OF;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_SIGNATURE_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.AXIOM;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_EXPRESSION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CreateQueryBuilder implements TranslationVisitor {

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  private final Map<Node, String> nodeVariableNameMapping = Maps.newHashMap();

  private final StringBuilder cypherString = new StringBuilder();

  public CreateQueryBuilder(@Nonnull VariableNameGenerator variableNameGenerator) {
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Override
  public void visit(@Nonnull Translation translation) {
    if (isDeclarationAxiomTranslation(translation)) {
      translation.edges().forEach(translateToCypher());
    } else {
      translation.edges()
          .filter(this::excludeEntityIriOrEntitySignatureOfEdge)
          .forEach(translateToCypher());
    }
  }

  private static boolean isDeclarationAxiomTranslation(Translation translation) {
    return translation.getTranslatedObject() instanceof OWLDeclarationAxiom;
  }

  private boolean excludeEntityIriOrEntitySignatureOfEdge(Edge edge) {
    return !(edge.isTypeOf(ENTITY_IRI) || edge.isTypeOf(ENTITY_SIGNATURE_OF));
  }

  @Nonnull
  private Consumer<Edge> translateToCypher() {
    return edge -> {
      var fromNode = edge.getFromNode();
      var createQueryForFromNode = translateNodeToCypher(fromNode);
      var toNode = edge.getToNode();
      var createQueryForToNode = translateNodeToCypher(toNode);
      var createQueryForConnectingEdge = translateEdgeToCypher(edge);
      cypherString.append(createQueryForFromNode)
          .append(createQueryForToNode)
          .append(createQueryForConnectingEdge);
    };
  }

  private String translateNodeToCypher(Node node) {
    var sb = new StringBuilder();
    if (!nodeVariableNameMapping.containsKey(node)) {
      var createKeyword = getCreateKeyword(node);
      sb.append(createKeyword).append(" ");
      appendTranslation(node, sb);
      sb.append("\n");
    }
    return sb.toString();
  }

  @Nonnull
  private String getCreateKeyword(Node node) {
    var createKeyword = "MERGE";
    if (isAxiom(node) || isAnnotation(node)) {
      createKeyword = "CREATE";
    } else if (isClassExpression(node)
        || isObjectPropertyExpression(node)
        || isDataPropertyExpression(node)) {
      createKeyword = isEntity(node) ? "MERGE" : "CREATE";
    }
    return createKeyword;
  }

  private static boolean isClassExpression(Node node) {
    return node.getLabels().isa(CLASS_EXPRESSION);
  }

  private static boolean isObjectPropertyExpression(Node node) {
    return node.getLabels().isa(OBJECT_PROPERTY_EXPRESSION);
  }

  private static boolean isDataPropertyExpression(Node node) {
    return node.getLabels().isa(DATA_PROPERTY_EXPRESSION);
  }

  private static boolean isEntity(Node node) {
    return node.getLabels().isa(ENTITY);
  }

  private static boolean isAxiom(Node node) {
    return node.getLabels().isa(AXIOM);
  }

  private static boolean isAnnotation(Node node) {
    return node.getLabels().isa(ANNOTATION);
  }

  @Nonnull
  private String getVariableName(Node node) {
    var variableName = nodeVariableNameMapping.get(node);
    if (variableName == null) {
      variableName = variableNameGenerator.generate();
      nodeVariableNameMapping.put(node, variableName);
    }
    return variableName;
  }

  private String translateEdgeToCypher(Edge edge) {
    var sb = new StringBuilder();
    sb.append("MERGE ");
    appendTranslation(edge.getFromNode(), sb);
    appendTranslation(edge, sb);
    appendTranslation(edge.getToNode(), sb);
    sb.append("\n");

    // Append an additional AXIOM edge if the method is currently processing the AXIOM_OF edge.
    if (edge.isTypeOf(AXIOM_OF)) {
      sb.append("MERGE ");
      appendTranslation(edge.getFromNode(), sb);
      sb.append("<-[:AXIOM {structuralSpec:true}]-");
      appendTranslation(edge.getToNode(), sb);
      sb.append("\n");
    }
    return sb.toString();
  }

  private void appendTranslation(Node node, StringBuilder sb) {
    sb.append("(");
    if (nodeVariableNameMapping.containsKey(node)) {
      sb.append(getVariableName(node));
    } else {
      sb.append(getVariableName(node)).append(node.printLabels()).append(" ").append(node.printProperties());
    }
    sb.append(")");
  }

  private void appendTranslation(Edge edge, StringBuilder sb) {
    sb.append("-[")
        .append(edge.printLabel()).append(" ")
        .append(edge.printProperties())
        .append("]->");
  }

  public String build() {
    return cypherString.toString();
  }
}
