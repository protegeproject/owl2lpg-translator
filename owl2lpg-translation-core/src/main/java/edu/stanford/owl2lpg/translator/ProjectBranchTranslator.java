package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.BRANCH;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.ONTOLOGY_DOCUMENT;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectBranchTranslator {

  @Nonnull
  private final ProjectIdNodeFactory projectIdNodeFactory;

  @Nonnull
  private final BranchIdNodeFactory branchIdNodeFactory;

  @Nonnull
  private final OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory;

  @Nonnull
  private final EdgeFactory edgeFactory;

  @Inject
  public ProjectBranchTranslator(@Nonnull ProjectIdNodeFactory projectIdNodeFactory,
                                 @Nonnull BranchIdNodeFactory branchIdNodeFactory,
                                 @Nonnull OntologyDocumentIdNodeFactory ontologyDocumentIdNodeFactory,
                                 @Nonnull EdgeFactory edgeFactory) {
    this.projectIdNodeFactory = checkNotNull(projectIdNodeFactory);
    this.branchIdNodeFactory = checkNotNull(branchIdNodeFactory);
    this.ontologyDocumentIdNodeFactory = checkNotNull(ontologyDocumentIdNodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Nonnull
  public Translation translate(@Nonnull ProjectId projectId,
                               @Nonnull BranchId branchId,
                               @Nonnull OntologyDocumentId ontologyDocumentId) {
    var projectNode = projectIdNodeFactory.createProjectNode(projectId);
    var branchNode = branchIdNodeFactory.createBranchNode(branchId);
    var ontologyDocumentNode = ontologyDocumentIdNodeFactory.createOntologyDocumentNode(ontologyDocumentId);
    return Translation.create(projectId,
        projectNode,
        ImmutableList.of(edgeFactory.createEdge(projectNode, branchNode, BRANCH)),
        ImmutableList.of(Translation.create(branchId,
            branchNode,
            ImmutableList.of(edgeFactory.createEdge(branchNode, ontologyDocumentNode, ONTOLOGY_DOCUMENT)),
            ImmutableList.of())));
  }
}
