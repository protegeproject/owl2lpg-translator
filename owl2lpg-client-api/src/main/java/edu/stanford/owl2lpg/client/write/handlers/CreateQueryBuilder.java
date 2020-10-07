package edu.stanford.owl2lpg.client.write.handlers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.OntologyDocumentId;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_SIGNATURE_OF;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.BRANCH;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROJECT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.STRUCTURAL_SPEC;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CreateQueryBuilder implements TranslationVisitor {

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId ontoDocId;

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  private final Map<Node, String> nodeVariableNameMapping = Maps.newHashMap();

  private final ImmutableList.Builder cypherStrings = new ImmutableList.Builder();

  public CreateQueryBuilder(@Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId ontoDocId,
                            @Nonnull VariableNameGenerator variableNameGenerator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.ontoDocId = checkNotNull(ontoDocId);
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Override
  public void visit(@Nonnull Translation translation) {
    cypherStrings.add(cypherQueryToCreateAxiom(translation));
    cypherStrings.add(cypherQueryToLinkAxiomToOntologyDocument(translation));
  }

  private String cypherQueryToCreateAxiom(@Nonnull Translation translation) {
    var sb = new StringBuilder();
    if (isDeclarationAxiomTranslation(translation)) {
      translation.edges()
          .map(this::translateToCypher)
          .forEach(sb::append);
    } else {
      translation.edges()
          .filter(this::excludeEntityIriOrEntitySignatureOfEdge)
          .map(this::translateToCypher)
          .forEach(sb::append);
    }
    return sb.toString();
  }


  private static boolean isDeclarationAxiomTranslation(Translation translation) {
    return translation.getTranslatedObject() instanceof OWLDeclarationAxiom;
  }

  private boolean excludeEntityIriOrEntitySignatureOfEdge(Edge edge) {
    return !(edge.isTypeOf(ENTITY_IRI) || edge.isTypeOf(ENTITY_SIGNATURE_OF));
  }

  @Nonnull
  private String translateToCypher(Edge edge) {
    var sb = new StringBuilder();
    sb.append(translateNodeToCypher(edge.getFromNode()))
        .append(translateNodeToCypher(edge.getToNode()))
        .append(translateEdgeToCypher(edge));
    return sb.toString();
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
    return node.getLabels().isa(NodeLabels.AXIOM);
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

  @Nonnull
  private String cypherQueryToLinkAxiomToOntologyDocument(Translation translation) {
    var sb = new StringBuilder();
    var ontoDocVariable = "o";
    var axiomNode = translation.getMainNode();
    var axiomVariable = getVariableName(axiomNode);
    sb.append(cypherQueryMatchOntologyDocument(ontoDocVariable));
    sb.append(cypherQueryMatchAxiom(axiomNode, axiomVariable));
    sb.append(cypherQueryMergeAxiomEdge(ontoDocVariable, axiomVariable));
    return sb.toString();
  }

  @Nonnull
  private String cypherQueryMatchOntologyDocument(String ontoDocVariable) {
    return "MATCH (" + PROJECT.getNeo4jName() + " {" + PROJECT_ID + ":" + projectId.printAsString() + "})-[" + EdgeLabel.BRANCH.getNeo4jName() + "]->" +
        "(" + BRANCH.getNeo4jName() + " {" + BRANCH_ID + ":" + branchId.printAsString() + "})-[" + EdgeLabel.ONTOLOGY_DOCUMENT.getNeo4jName() + "]->" +
        "(" + ontoDocVariable + ONTOLOGY_DOCUMENT.getNeo4jName() + " {" + ONTOLOGY_DOCUMENT_ID + ":" + ontoDocId.printAsString() + "})\n";
  }

  @Nonnull
  private String cypherQueryMatchAxiom(Node axiomNode, String axiomVariable) {
    return "MATCH (" + axiomVariable + axiomNode.printLabels() + " " + axiomNode.printProperties() + ")\n";
  }

  @Nonnull
  private String cypherQueryMergeAxiomEdge(String ontoDocVariable, String axiomVariable) {
    return "MERGE (" + ontoDocVariable + ")-[" + EdgeLabel.AXIOM.getNeo4jName() + " {" + STRUCTURAL_SPEC + ":true}]->(" + axiomVariable + ")";
  }

  public ImmutableList<String> build() {
    return cypherStrings.build();
  }
}
