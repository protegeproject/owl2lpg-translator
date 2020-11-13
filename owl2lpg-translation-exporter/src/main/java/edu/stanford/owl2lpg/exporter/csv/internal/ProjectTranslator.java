package edu.stanford.owl2lpg.exporter.csv.internal;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.project.BranchId;
import edu.stanford.bmir.protege.web.shared.project.OntologyDocumentId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.model.StructuralEdgeFactory;
import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.shared.BytesDigester;
import edu.stanford.owl2lpg.translator.shared.OntologyObjectDigester;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.BRANCH;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROJECT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.DIGEST;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IS_DEFAULT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectTranslator {

  @Nonnull
  private final StructuralEdgeFactory edgeFactory;

  @Nonnull
  private final OntologyObjectDigester ontologyObjectDigester;

  @Nonnull
  private final BytesDigester bytesDigester;

  @Inject
  public ProjectTranslator(@Nonnull StructuralEdgeFactory edgeFactory,
                           @Nonnull OntologyObjectDigester ontologyObjectDigester,
                           @Nonnull BytesDigester bytesDigester) {
    this.edgeFactory = checkNotNull(edgeFactory);
    this.ontologyObjectDigester = checkNotNull(ontologyObjectDigester);
    this.bytesDigester = checkNotNull(bytesDigester);
  }

  @Nonnull
  public Translation translate(@Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId documentId) {
    return translate(new OWLOntologyID(), projectId, branchId, documentId);
  }

  @Nonnull
  public Translation translate(@Nonnull OWLOntologyID ontologyId,
                               @Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId documentId) {
    var projectNode = createProjectNode(projectId);
    var branchNode = createBranchNode(branchId);
    var documentNode = createDocumentNode(documentId);

    var ontologyIdEdge = new ImmutableList.Builder<Edge>();
    var ontologyIdTranslation = new ImmutableList.Builder<Translation>();
    var ontologyIri = ontologyId.getOntologyIRI();
    if (ontologyIri.isPresent()) {
      createOntologyIriTranslationAndEdge(ontologyIri.get(), documentNode, ontologyIdTranslation, ontologyIdEdge);
    }
    var versionIri = ontologyId.getVersionIRI();
    if (versionIri.isPresent()) {
      createVersionIriTranslationAndEdge(versionIri.get(), documentNode, ontologyIdTranslation, ontologyIdEdge);
    }

    return Translation.create(projectId,
        projectNode,
        ImmutableList.of(edgeFactory.getBranchEdge(projectNode, branchNode)),
        ImmutableList.of(Translation.create(branchId,
            branchNode,
            ImmutableList.of(edgeFactory.getOntologyDocumentEdge(branchNode, documentNode)),
            ImmutableList.of(Translation.create(documentId,
                documentNode,
                ontologyIdEdge.build(),
                ontologyIdTranslation.build())))));
  }

  @Nonnull
  private Node createProjectNode(ProjectId projectId) {
    var digestString = bytesDigester.getDigestString(projectId.getId().getBytes());
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId,
        PROJECT,
        Properties.of(
            PROJECT_ID, projectId.getId(),
            DIGEST, digestString));
  }

  @Nonnull
  private Node createBranchNode(BranchId branchId) {
    var digestString = bytesDigester.getDigestString(branchId.getId().getBytes());
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId,
        BRANCH,
        Properties.of(
            BRANCH_ID, branchId.getId(),
            IS_DEFAULT, branchId.isDefault(),
            DIGEST, digestString));
  }

  @Nonnull
  private Node createDocumentNode(OntologyDocumentId ontDocId) {
    var digestString = bytesDigester.getDigestString(ontDocId.getId().getBytes());
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId,
        ONTOLOGY_DOCUMENT,
        Properties.of(
            ONTOLOGY_DOCUMENT_ID, ontDocId.getId(),
            IS_DEFAULT, ontDocId.isDefault(),
            DIGEST, digestString));
  }

  private void createOntologyIriTranslationAndEdge(IRI ontologyIri, Node documentNode,
                                                   ImmutableList.Builder<Translation> ontologyIdTranslation,
                                                   ImmutableList.Builder<Edge> ontologyIdEdge) {
    var ontologyIriNode = createIriNode(ontologyIri);
    var ontologyIriTranslation = Translation.create(ontologyIri, ontologyIriNode);
    var ontologyIriEdge = edgeFactory.getOntologyIriEdge(documentNode, ontologyIriNode);
    ontologyIdTranslation.add(ontologyIriTranslation);
    ontologyIdEdge.add(ontologyIriEdge);
  }

  private void createVersionIriTranslationAndEdge(IRI versionIri, Node documentNode,
                                                  ImmutableList.Builder<Translation> ontologyIdTranslation,
                                                  ImmutableList.Builder<Edge> ontologyIdEdge) {
    var versionIriNode = createIriNode(versionIri);
    var versionIriTranslation = Translation.create(versionIri, versionIriNode);
    var VersionIriEdge = edgeFactory.getVersionIriEdge(documentNode, versionIriNode);
    ontologyIdTranslation.add(versionIriTranslation);
    ontologyIdEdge.add(VersionIriEdge);
  }


  private Node createIriNode(IRI iri) {
    var digestString = ontologyObjectDigester.getDigest(iri);
    var nodeId = NodeId.create(digestString);
    return Node.create(nodeId,
        NodeLabels.IRI,
        Properties.of(
            PropertyFields.IRI, String.valueOf(iri),
            DIGEST, digestString));
  }
}
