package edu.stanford.owl2lpg.client.write.handlers;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM_OF;
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

  private final StringBuilder stringBuilder = new StringBuilder();

  public CreateQueryBuilder(@Nonnull VariableNameGenerator variableNameGenerator) {
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Override
  public void visit(@Nonnull Translation translation) {
    translation.edges()
        .forEach(edge -> {
          var fromNode = edge.getFromNode();
          translate(fromNode);
          var toNode = edge.getToNode();
          translate(toNode);
          translate(edge);
        });
  }

  private void translate(Node node) {
    if (!nodeVariableNameMapping.containsKey(node)) {
      if (isAxiom(node) || isAnnotation(node)) {
        stringBuilder.append("CREATE ");
      } else {
        stringBuilder.append("MERGE ");
      }
      var variableName = getVariableName(node);
      stringBuilder.append("(")
          .append(variableName)
          .append(node.printLabels()).append(" ").append(node.printProperties())
          .append(")\n");
    }
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

  private void translate(Edge edge) {
    stringBuilder.append("MERGE ")
        .append("(").append(getVariableName(edge.getFromNode())).append(")")
        .append("-[").append(edge.printLabel()).append(" ").append(edge.printProperties()).append("]->")
        .append("(").append(getVariableName(edge.getToNode())).append(")\n");
    if (edge.isTypeOf(AXIOM_OF)) {
      addAdditionalAxiomOfMergeQuery(edge);
    }
  }

  private void addAdditionalAxiomOfMergeQuery(Edge edge) {
    stringBuilder.append("MERGE ")
        .append("(").append(getVariableName(edge.getFromNode())).append(")")
        .append("<-[:AXIOM {structuralSpec:true}]-")
        .append("(").append(getVariableName(edge.getToNode())).append(")\n");
  }

  public String build() {
    return stringBuilder.toString();
  }
}
