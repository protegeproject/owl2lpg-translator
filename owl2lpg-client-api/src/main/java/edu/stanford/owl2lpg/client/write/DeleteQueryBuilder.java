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
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ENTITY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.IN_ONTOLOGY_SIGNATURE;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ONTOLOGY_IRI;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.VERSION_IRI;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.BRANCH;
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
public class DeleteQueryBuilder implements TranslationVisitor {

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

  private final Map<Edge, String> edgeVariableNameMapping = Maps.newHashMap();

  private final ImmutableList.Builder<String> cypherStrings = new ImmutableList.Builder<String>();
  public static final String DOCUMENT_VARIABLE = "o";

  public DeleteQueryBuilder(@Nonnull ProjectId projectId,
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
  public void visit(@Nonnull Translation translation) {
    cypherStrings.add(cypherQueryToDeleteAllEdges(translation));
    cypherStrings.add(cypherQueryToDeleteOrphanNodes());
  }

  @Nonnull
  private String cypherQueryToDeleteAllEdges(Translation translation) {
    var sb = new StringBuilder();
    if (isDeclarationAxiomTranslation(translation)) {
      translation.edges()
          .map(this::translateToCypher)
          .forEach(sb::append);
    } else {
      translation.edges()
          .filter(this::excludeEntityIriOrInOntologySignatureEdge)
          .map(this::translateToCypher)
          .forEach(sb::append);
    }
    var axiomVariable = getVariableName(translation.getMainNode());
    sb.append(cypherQueryMergeOntologyDocument(DOCUMENT_VARIABLE));
    sb.append(cypherQueryMatchAxiomEdge(DOCUMENT_VARIABLE, axiomVariable));
    var edgeVariables = edgeVariableNameMapping.values();
    sb.append("DELETE ")
        .append(String.join(",", edgeVariables))
        .append("\n");
    return sb.toString();
  }

  private static boolean isDeclarationAxiomTranslation(Translation translation) {
    return translation.getTranslatedObject() instanceof OWLDeclarationAxiom;
  }

  private boolean excludeEntityIriOrInOntologySignatureEdge(Edge edge) {
    return !(edge.isTypeOf(ENTITY_IRI) || edge.isTypeOf(IN_ONTOLOGY_SIGNATURE));
  }

  @Nonnull
  private String translateToCypher(Edge edge) {
    var sb = new StringBuilder();
    sb.append("MATCH ");
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
        .append(getVariableName(edge))
        .append(edge.printLabel()).append(" ")
        .append(edge.printProperties())
        .append("]->");
  }

  @Nonnull
  private String cypherQueryToDeleteOrphanNodes() {
    return "MATCH (n) WHERE NOT (n)--() DELETE n";
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
  private String cypherQueryMergeOntologyDocument(String documentVariable) {
    var sb = new StringBuilder();

    var projectVariable = "p";
    var branchVariable = "b";
    sb.append("MERGE (").append(projectVariable).append(PROJECT.toNeo4jLabel())
        .append(" {").append(PROJECT_ID).append(":").append(projectId.toQuotedString()).append("})\n");
    sb.append("MERGE (").append(branchVariable).append(BRANCH.toNeo4jLabel())
        .append(" {").append(BRANCH_ID).append(":").append(branchId.toQuotedString()).append("})\n");
    sb.append("MERGE (").append(documentVariable).append(ONTOLOGY_DOCUMENT.toNeo4jLabel())
        .append(" {").append(ONTOLOGY_DOCUMENT_ID).append(":").append(documentId.toQuotedString()).append("})\n");
    sb.append("MERGE (").append(projectVariable).append(")-[").append(EdgeLabel.BRANCH.toNeo4jLabel()).append("]->")
        .append("(").append(branchVariable).append(")-[").append(EdgeLabel.ONTOLOGY_DOCUMENT.toNeo4jLabel()).append("]->")
        .append("(").append(documentVariable).append(")\n");

    var ontologyIriVariable = "i";
    var ontologyIri = ontologyId.getOntologyIRI();
    if (ontologyIri.isPresent()) {
      sb.append("MERGE (").append(ontologyIriVariable).append(IRI.toNeo4jLabel())
          .append(" {").append(PropertyFields.IRI).append(":").append(ontologyIri.get().toQuotedString()).append("})\n");
      sb.append("MERGE (").append(documentVariable).append(")-[").append(ONTOLOGY_IRI.toNeo4jLabel()).append("]->")
          .append("(").append(ontologyIriVariable).append(")\n");
    }

    var versionIriVariable = "v";
    var versionIri = ontologyId.getVersionIRI();
    if (versionIri.isPresent()) {
      sb.append("MERGE (").append(versionIriVariable).append(IRI.toNeo4jLabel())
          .append(" {").append(PropertyFields.IRI).append(":").append(versionIri.get().toQuotedString()).append("})\n");
      sb.append("MERGE (").append(documentVariable).append(")-[").append(VERSION_IRI.toNeo4jLabel()).append("]->")
          .append("(").append(versionIriVariable).append(")\n");
    }

    return sb.toString();
  }

  @Nonnull
  private String cypherQueryMatchAxiomEdge(String documentVariable, String axiomVariable) {
    return "MATCH (" + documentVariable + ")-[" + EdgeLabel.AXIOM.toNeo4jLabel() + " {" + STRUCTURAL_SPEC + ":true}]->(" + axiomVariable + ")\n";
  }

  @Nonnull
  public ImmutableList<String> build() {
    return cypherStrings.build();
  }
}
