package edu.stanford.owl2lpg.client.write.changes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.IN_ONTOLOGY_SIGNATURE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.BRANCH;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROJECT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CreateQueryBuilder implements TranslationVisitor {

  public static final String DOCUMENT_VARIABLE = "o";
  public static final String PROJECT_VARIABLE = "p";
  public static final String BRANCH_VARIABLE = "b";

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId documentId;

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  private final Map<Node, String> nodeVariableNameMapping = Maps.newHashMap();

  private final ImmutableList.Builder<String> cypherStrings = ImmutableList.builder();

  public CreateQueryBuilder(@Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId documentId,
                            @Nonnull VariableNameGenerator variableNameGenerator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentId = checkNotNull(documentId);
    this.variableNameGenerator = checkNotNull(variableNameGenerator);
  }

  @Override
  public void visit(@Nonnull Translation axiomTranslation) {
    cypherStrings.add(cypherQueryToMergeOntologyDocument());
    cypherStrings.add(cypherQueryToCreateAxiom(axiomTranslation));
    axiomTranslation.nodes(ENTITY).forEach(entityNode ->
        cypherStrings.add(cypherQueryToLinkEntityToOntologyDocument(entityNode)));
    cypherStrings.add(cypherQueryToLinkAxiomToOntologyDocument(axiomTranslation));
  }

  private String cypherQueryToCreateAxiom(@Nonnull Translation translation) {
    var sb = new StringBuilder();
    translation.edges()
        .map(this::translateToCypher)
        .forEach(sb::append);
    return sb.toString();
  }

  @Nonnull
  private String translateToCypher(Edge edge) {
    return translateToCypher(edge.getFromNode()) +
        translateToCypher(edge.getToNode()) +
        translateEdgeToCypher(edge);
  }

  @Nonnull
  private String translateToCypher(Node node) {
    var sb = new StringBuilder();
    if (!nodeVariableNameMapping.containsKey(node)) {
      sb.append("MERGE ");
      cypherNode(node, sb);
      sb.append("\n");
    }
    return sb.toString();
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
    cypherNode(edge.getFromNode(), sb);
    cypherEdge(edge, sb);
    cypherNode(edge.getToNode(), sb);
    sb.append("\n");
    return sb.toString();
  }

  private void cypherNode(Node node, StringBuilder sb) {
    sb.append("(");
    if (nodeVariableNameMapping.containsKey(node)) {
      sb.append(getVariableName(node));
    } else {
      sb.append(getVariableName(node)).append(node.printLabels()).append(" ").append(node.printProperties());
    }
    sb.append(")");
  }

  private void cypherEdge(Edge edge, StringBuilder sb) {
    sb.append("-[")
        .append(edge.printLabel()).append(" ")
        .append(edge.printProperties())
        .append("]->");
  }

  @Nonnull
  private String cypherQueryToMergeOntologyDocument() {
    return "MERGE (" + PROJECT_VARIABLE + PROJECT.toNeo4jLabel() +
        " {" + PROJECT_ID + ":" + projectId.toQuotedString() + "})\n" +
        "MERGE (" + BRANCH_VARIABLE + BRANCH.toNeo4jLabel() +
        " {" + BRANCH_ID + ":" + branchId.toQuotedString() + "})\n" +
        "MERGE (" + PROJECT_VARIABLE + ")-[" + EdgeLabel.BRANCH.toNeo4jLabel() + "]->(" + BRANCH_VARIABLE + ")\n" +
        "MERGE (" + DOCUMENT_VARIABLE + ONTOLOGY_DOCUMENT.toNeo4jLabel() +
        " {" + ONTOLOGY_DOCUMENT_ID + ":" + documentId.toQuotedString() + "})\n" +
        "MERGE (" + BRANCH_VARIABLE + ")-[" + EdgeLabel.ONTOLOGY_DOCUMENT.toNeo4jLabel() + "]->(" + DOCUMENT_VARIABLE + ")\n";
  }

  @Nonnull
  private String cypherQueryToLinkEntityToOntologyDocument(Node entityNode) {
    var sb = new StringBuilder();
    var entityVariable = getVariableName(entityNode);
    sb.append(cypherQueryMatchOntologyDocument());
    sb.append(cypherQueryMatchNode(entityNode, entityVariable));
    sb.append(cypherQueryMergeInOntologySignatureEdge(entityVariable));
    return sb.toString();
  }

  @Nonnull
  private String cypherQueryToLinkAxiomToOntologyDocument(Translation translation) {
    var sb = new StringBuilder();
    var axiomNode = translation.getMainNode();
    var axiomVariable = getVariableName(axiomNode);
    sb.append(cypherQueryMatchOntologyDocument());
    sb.append(cypherQueryMatchNode(axiomNode, axiomVariable));
    sb.append(cypherQueryMergeAxiomEdge(axiomVariable));
    return sb.toString();
  }

  private String cypherQueryMatchOntologyDocument() {
    return "MATCH (" + DOCUMENT_VARIABLE + ONTOLOGY_DOCUMENT.toNeo4jLabel() + " {" + ONTOLOGY_DOCUMENT_ID + ":" + documentId.toQuotedString() + "})\n";
  }

  @Nonnull
  private String cypherQueryMatchNode(Node axiomNode, String axiomVariable) {
    return "MATCH (" + axiomVariable + axiomNode.printLabels() + " " + axiomNode.printProperties() + ")\n";
  }

  @Nonnull
  private String cypherQueryMergeInOntologySignatureEdge(String entityVariable) {
    return "MERGE (" + entityVariable + ")-[" + IN_ONTOLOGY_SIGNATURE.toNeo4jLabel() + "]->(" + DOCUMENT_VARIABLE + ")\n";
  }

  @Nonnull
  private String cypherQueryMergeAxiomEdge(String axiomVariable) {
    return "MERGE (" + DOCUMENT_VARIABLE + ")-[" + AXIOM.toNeo4jLabel() + "]->(" + axiomVariable + ")";
  }

  public ImmutableList<String> build() {
    return cypherStrings.build();
  }
}
