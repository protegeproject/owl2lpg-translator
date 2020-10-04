package edu.stanford.owl2lpg.client.write.handlers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_SIGNATURE_OF;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DeleteQueryBuilder implements TranslationVisitor {

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  private final Map<Edge, String> edgeVariableNameMapping = Maps.newHashMap();

  private final ImmutableList.Builder cypherStrings = new ImmutableList.Builder();

  public DeleteQueryBuilder(@Nonnull VariableNameGenerator variableNameGenerator) {
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Override
  public void visit(@Nonnull Translation translation) {
    if (isDeclarationAxiomTranslation(translation)) {
      translation.edges()
          .map(this::translateToCypher)
          .forEach(cypherStrings::add);
    } else {
      translation.edges()
          .filter(this::excludeEntityIriOrEntitySignatureOfEdge)
          .map(this::translateToCypher)
          .forEach(cypherStrings::add);
    }
    cypherStrings.add(cypherQueryToDeleteOrphanNodes());
  }

  private static boolean isDeclarationAxiomTranslation(Translation translation) {
    return translation.getTranslatedObject() instanceof OWLDeclarationAxiom;
  }

  private boolean excludeEntityIriOrEntitySignatureOfEdge(Edge edge) {
    return !(edge.isTypeOf(ENTITY_IRI) || edge.isTypeOf(ENTITY_SIGNATURE_OF));
  }

  @Nonnull
  private String translateToCypher(Edge edge) {
    var fromNode = edge.getFromNode();
    var toNode = edge.getToNode();
    var sb = new StringBuilder();
    sb.append("MATCH ")
        .append("(").append(fromNode.printLabels()).append(" ").append(fromNode.printProperties()).append(")")
        .append("-[").append(getVariableName(edge)).append(edge.printLabel()).append(" ").append(edge.printProperties()).append("]->")
        .append("(").append(toNode.printLabels()).append(" ").append(toNode.printProperties()).append(")\n")
        .append("DELETE ").append(getVariableName(edge));
    return sb.toString();
  }

  private String cypherQueryToDeleteOrphanNodes() {
    return "MATCH (n) WHERE NOT (n)--() DELETE n";
  }

  @Nonnull
  private String getVariableName(Edge edge) {
    var variableName = edgeVariableNameMapping.get(edge);
    if (variableName == null) {
      variableName = variableNameGenerator.generate("r");
      edgeVariableNameMapping.put(edge, variableName);
    }
    return variableName;
  }

  @Nonnull
  public ImmutableList<String> build() {
    return cypherStrings.build();
  }
}
