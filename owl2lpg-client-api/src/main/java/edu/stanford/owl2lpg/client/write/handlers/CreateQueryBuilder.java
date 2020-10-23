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
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
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
public class CreateQueryBuilder implements TranslationVisitor {

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

  private final ImmutableList.Builder cypherStrings = new ImmutableList.Builder();

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
  public void visit(@Nonnull Translation translation) {
    cypherStrings.add(cypherQueryToCreateAxiom(translation));
    cypherStrings.add(cypherQueryToLinkAxiomToOntologyDocument(translation));
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
    sb.append(translateNodeToCypher(edge.getFromNode()))
        .append(translateNodeToCypher(edge.getToNode()))
        .append(translateEdgeToCypher(edge));
    return sb.toString();
  }

  private String translateNodeToCypher(Node node) {
    var sb = new StringBuilder();
    if (!nodeVariableNameMapping.containsKey(node)) {
      sb.append("MERGE ");
      appendTranslation(node, sb);
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
    sb.append(cypherQueryMatchAxiom(axiomNode, axiomVariable));
    sb.append(cypherQueryMergeOntologyDocument(ontoDocVariable));
    sb.append(cypherQueryMergeAxiomEdge(ontoDocVariable, axiomVariable));
    return sb.toString();
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
          .append(" {").append(PropertyFields.IRI).append(":\"").append(ontologyIri.get().toString()).append("\"})\n");
      sb.append("MERGE (").append(documentVariable).append(")-[").append(ONTOLOGY_IRI.toNeo4jLabel()).append("]->")
          .append("(").append(ontologyIriVariable).append(")\n");
    }

    var versionIriVariable = "v";
    var versionIri = ontologyId.getVersionIRI();
    if (versionIri.isPresent()) {
      sb.append("MERGE (").append(versionIriVariable).append(IRI.toNeo4jLabel())
          .append(" {").append(PropertyFields.IRI).append(":\"").append(versionIri.get().toString()).append("\"})\n");
      sb.append("MERGE (").append(documentVariable).append(")-[").append(VERSION_IRI.toNeo4jLabel()).append("]->")
          .append("(").append(versionIriVariable).append(")\n");
    }

    return sb.toString();
  }

  @Nonnull
  private String cypherQueryMatchAxiom(Node axiomNode, String axiomVariable) {
    return "MATCH (" + axiomVariable + axiomNode.printLabels() + " " + axiomNode.printProperties() + ")\n";
  }

  @Nonnull
  private String cypherQueryMergeAxiomEdge(String ontoDocVariable, String axiomVariable) {
    return "MERGE (" + ontoDocVariable + ")-[" + EdgeLabel.AXIOM.toNeo4jLabel() + " {" + STRUCTURAL_SPEC + ":true}]->(" + axiomVariable + ")";
  }

  public ImmutableList build() {
    return cypherStrings.build();
  }
}
