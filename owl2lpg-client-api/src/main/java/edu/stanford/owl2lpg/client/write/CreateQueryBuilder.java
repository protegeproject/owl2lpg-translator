package edu.stanford.owl2lpg.client.write;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.translator.shared.BranchId;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.shared.OntologyDocumentId;
import edu.stanford.owl2lpg.translator.shared.ProjectId;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.model.TranslationVisitor;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.AXIOM;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.IN_ONTOLOGY_SIGNATURE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ONTOLOGY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.VERSION_IRI;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.BRANCH;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.IRI;
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

  public static final String DOCUMENT_VARIABLE = "o";
  public static final String PROJECT_VARIABLE = "p";
  public static final String BRANCH_VARIABLE = "b";
  public static final String ONTOLOGY_IRI_VARIABLE = "i";
  public static final String VERSION_IRI_VARIABLE = "v";

  @Nonnull
  private final ProjectId projectId;

  @Nonnull
  private final BranchId branchId;

  @Nonnull
  private final OntologyDocumentId documentId;

  @Nonnull
  private final OWLOntologyID ontologyId;

  @Nonnull
  private final VariableNameGenerator variableNameGenerator;

  private final Map<Node, String> nodeVariableNameMapping = Maps.newHashMap();

  private final ImmutableList.Builder<String> cypherStrings = new ImmutableList.Builder<String>();

  public CreateQueryBuilder(@Nonnull ProjectId projectId,
                            @Nonnull BranchId branchId,
                            @Nonnull OntologyDocumentId documentId,
                            @Nonnull OWLOntologyID ontologyId,
                            @Nonnull VariableNameGenerator variableNameGenerator) {
    this.projectId = checkNotNull(projectId);
    this.branchId = checkNotNull(branchId);
    this.documentId = checkNotNull(documentId);
    this.ontologyId = checkNotNull(ontologyId);
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
    var sb = new StringBuilder();
    sb.append(translateToCypher(edge.getFromNode()))
        .append(translateToCypher(edge.getToNode()))
        .append(translateEdgeToCypher(edge));
    return sb.toString();
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
    var sb = new StringBuilder();
    sb.append("MERGE (").append(PROJECT_VARIABLE).append(PROJECT.toNeo4jLabel())
        .append(" {").append(PROJECT_ID).append(":").append(projectId.toQuotedString()).append("})\n");
    sb.append("MERGE (").append(BRANCH_VARIABLE).append(BRANCH.toNeo4jLabel())
        .append(" {").append(BRANCH_ID).append(":").append(branchId.toQuotedString()).append("})\n");
    sb.append("MERGE (").append(DOCUMENT_VARIABLE).append(ONTOLOGY_DOCUMENT.toNeo4jLabel())
        .append(" {").append(ONTOLOGY_DOCUMENT_ID).append(":").append(documentId.toQuotedString()).append("})\n");
    sb.append("MERGE (").append(PROJECT_VARIABLE).append(")-[").append(EdgeLabel.BRANCH.toNeo4jLabel()).append("]->")
        .append("(").append(BRANCH_VARIABLE).append(")-[").append(EdgeLabel.ONTOLOGY_DOCUMENT.toNeo4jLabel()).append("]->")
        .append("(").append(DOCUMENT_VARIABLE).append(")\n");

    var ontologyIri = ontologyId.getOntologyIRI();
    if (ontologyIri.isPresent()) {
      sb.append("MERGE (").append(ONTOLOGY_IRI_VARIABLE).append(IRI.toNeo4jLabel())
          .append(" {").append(PropertyFields.IRI).append(":\"").append(ontologyIri.get().toString()).append("\"})\n");
      sb.append("MERGE (").append(DOCUMENT_VARIABLE).append(")-[").append(ONTOLOGY_IRI.toNeo4jLabel()).append("]->")
          .append("(").append(ONTOLOGY_IRI_VARIABLE).append(")\n");
    }

    var versionIri = ontologyId.getVersionIRI();
    if (versionIri.isPresent()) {
      sb.append("MERGE (").append(VERSION_IRI_VARIABLE).append(IRI.toNeo4jLabel())
          .append(" {").append(PropertyFields.IRI).append(":\"").append(versionIri.get().toString()).append("\"})\n");
      sb.append("MERGE (").append(DOCUMENT_VARIABLE).append(")-[").append(VERSION_IRI.toNeo4jLabel()).append("]->")
          .append("(").append(VERSION_IRI_VARIABLE).append(")\n");
    }
    return sb.toString();
  }

  @Nonnull
  private String cypherQueryToLinkEntityToOntologyDocument(Node entityNode) {
    var sb = new StringBuilder();
    var entityVariable = getVariableName(entityNode);
    sb.append(cypherQueryMatchOntologyDocument(DOCUMENT_VARIABLE));
    sb.append(cypherQueryMatchNode(entityNode, entityVariable));
    sb.append(cypherQueryMergeInOntologySignatureEdge(entityVariable, DOCUMENT_VARIABLE));
    return sb.toString();
  }

  @Nonnull
  private String cypherQueryToLinkAxiomToOntologyDocument(Translation translation) {
    var sb = new StringBuilder();
    var axiomNode = translation.getMainNode();
    var axiomVariable = getVariableName(axiomNode);
    sb.append(cypherQueryMatchOntologyDocument(DOCUMENT_VARIABLE));
    sb.append(cypherQueryMatchNode(axiomNode, axiomVariable));
    sb.append(cypherQueryMergeAxiomEdge(DOCUMENT_VARIABLE, axiomVariable));
    return sb.toString();
  }

  private String cypherQueryMatchOntologyDocument(String documentVariable) {
    return "MATCH (" + documentVariable + ONTOLOGY_DOCUMENT.toNeo4jLabel() + " {" + ONTOLOGY_DOCUMENT_ID + ":" + documentId.toQuotedString() + "})\n";
  }

  @Nonnull
  private String cypherQueryMatchNode(Node axiomNode, String axiomVariable) {
    return "MATCH (" + axiomVariable + axiomNode.printLabels() + " " + axiomNode.printProperties() + ")\n";
  }

  @Nonnull
  private String cypherQueryMergeInOntologySignatureEdge(String entityVariable, String documentVariable) {
    return "MERGE (" + entityVariable + ")-[" + IN_ONTOLOGY_SIGNATURE.toNeo4jLabel() + "]->(" + documentVariable + ")\n";
  }

  @Nonnull
  private String cypherQueryMergeAxiomEdge(String documentVariable, String axiomVariable) {
    return "MERGE (" + documentVariable + ")-[" + AXIOM.toNeo4jLabel() + " {" + STRUCTURAL_SPEC + ":true}]->(" + axiomVariable + ")";
  }

  public ImmutableList<String> build() {
    return cypherStrings.build();
  }
}
