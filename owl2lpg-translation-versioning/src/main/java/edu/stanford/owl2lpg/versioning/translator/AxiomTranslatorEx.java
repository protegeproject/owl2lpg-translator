package edu.stanford.owl2lpg.versioning.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.AxiomTranslator;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.model.BranchId;
import edu.stanford.owl2lpg.versioning.model.OntologyDocumentId;
import edu.stanford.owl2lpg.versioning.model.ProjectId;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomTranslatorEx extends AxiomTranslator {

  public AxiomTranslatorEx(@Nonnull OWLAxiomVisitorEx<Translation> visitor) {
    super(visitor);
  }

  @Nonnull
  public Translation translate(@Nonnull AxiomContext context, @Nonnull OWLAxiom axiom) {
    checkNotNull(context);
    var axiomTranslation = translate(axiom);
    var docNode = createOntologyDocumentNode(context.getOntologyDocumentId());
    var documentTranslation = Translation.create(docNode)
        .connectWith(axiomTranslation, EdgeLabel.AXIOM, Properties.empty());
    var branchNode = createBranchNode(context.getBranchId());
    var branchTranslation = Translation.create(branchNode)
        .connectWith(documentTranslation, EdgeLabel.ONTOLOGY_DOCUMENT, Properties.empty());
    var projectNode = createProjectNode(context.getProjectId());
    var projectTranslation = Translation.create(projectNode)
        .connectWith(branchTranslation, EdgeLabel.BRANCH, Properties.empty());
    return projectTranslation;
  }

  @Nonnull
  private Node createProjectNode(@Nonnull ProjectId projectId) {
    return Node.create(
        NodeId.create(projectId),
        NodeLabels.PROJECT,
        Properties.of(PropertyFields.PROJECT_ID, String.valueOf(projectId.getIdentifier())));
  }

  @Nonnull
  private Node createBranchNode(@Nonnull BranchId branchId) {
    return Node.create(
        NodeId.create(branchId),
        NodeLabels.BRANCH,
        Properties.of(PropertyFields.BRANCH_ID, String.valueOf(branchId.getIdentifier())));
  }

  @Nonnull
  private Node createOntologyDocumentNode(@Nonnull OntologyDocumentId docId) {
    return Node.create(
        NodeId.create(docId),
        NodeLabels.ONTOLOGY_DOCUMENT,
        Properties.of(PropertyFields.ONTOLOGY_DOCUMENT_ID, String.valueOf(docId.getIdentifier())));
  }

  public static class NodeLabels {
    public static final ImmutableList<String> PROJECT = ImmutableList.of("Project");
    public static final ImmutableList<String> BRANCH = ImmutableList.of("Branch");
    public static final ImmutableList<String> ONTOLOGY_DOCUMENT = ImmutableList.of("OntologyDocument");
  }

  public static class EdgeLabels {
    public static final String PROJECT = "project";
    public static final String BRANCH = "branch";
    public static final String ONTOLOGY_DOCUMENT = "ontologyDocument";
    public static final String AXIOM = "axiom";
  }

  public static class PropertyFields {
    public static final String PROJECT_ID = "projectId";
    public static final String BRANCH_ID = "branchId";
    public static final String ONTOLOGY_DOCUMENT_ID = "ontologyDocumentId";
  }
}
