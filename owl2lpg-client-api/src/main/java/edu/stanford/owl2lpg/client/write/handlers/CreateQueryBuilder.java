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
      if (isAxiom(node) || isAnnotation(node)) {
        sb.append("CREATE ");
      } else {
        sb.append("MERGE ");
      }
      var variableName = getVariableName(node);
      sb.append("(")
          .append(variableName)
          .append(node.printLabels()).append(" ").append(node.printProperties())
          .append(")\n");
    }
    return sb.toString();
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
    sb.append("MERGE ")
        .append("(").append(getVariableName(edge.getFromNode())).append(")")
        .append("-[").append(edge.printLabel()).append(" ").append(edge.printProperties()).append("]->")
        .append("(").append(getVariableName(edge.getToNode())).append(")\n");
    if (edge.isTypeOf(AXIOM_OF)) {
      sb.append("MERGE ")
          .append("(").append(getVariableName(edge.getFromNode())).append(")")
          .append("<-[:AXIOM {structuralSpec:true}]-")
          .append("(").append(getVariableName(edge.getToNode())).append(")\n");
    }
    return sb.toString();
  }

  public String build() {
    return cypherString.toString();
  }
}
