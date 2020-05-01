package edu.stanford.owl2lpg.versioning.translator;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.TranslationBuilder;
import edu.stanford.owl2lpg.versioning.model.AxiomContext;
import edu.stanford.owl2lpg.versioning.model.BranchId;
import edu.stanford.owl2lpg.versioning.model.OntologyDocumentId;
import edu.stanford.owl2lpg.versioning.model.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.utils.PropertiesFactory.Properties;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AxiomContextTranslator {

  @Nonnull
  public Translation translate(@Nonnull AxiomContext context) {
    checkNotNull(context);
    var projectNode = createProjectNode(context.getProjectId());
    var branchNode = createBranchNode(context.getBranchId());
    var docNode = createOntologyDocumentNode(context.getOntologyDocumentId());
    var builder = new TranslationBuilder();
    return builder.add(projectNode, branchNode, EdgeLabels.BRANCH)
        .add(branchNode, docNode, EdgeLabels.ONTOLOGY_DOCUMENT)
        .build();
  }

  @Nonnull
  public Node createProjectNode(@Nonnull ProjectId projectId) {
    checkNotNull(projectId);
    return Node.create(
        NodeId.create(projectId),
        NodeLabels.PROJECT,
        Properties(PropertyFields.PROJECT_ID, projectId.getIdentifier().toString()));
  }

  @Nonnull
  public Node createBranchNode(@Nonnull BranchId branchId) {
    checkNotNull(branchId);
    return Node.create(
        NodeId.create(branchId),
        NodeLabels.BRANCH,
        Properties(PropertyFields.BRANCH_ID, branchId.getIdentifier().toString()));
  }

  @Nonnull
  public Node createOntologyDocumentNode(@Nonnull OntologyDocumentId docId) {
    checkNotNull(docId);
    return Node.create(
        NodeId.create(docId),
        NodeLabels.ONTOLOGY_DOCUMENT,
        Properties(PropertyFields.ONTOLOGY_DOCUMENT_ID, docId.getIdentifier().toString()));
  }
}
