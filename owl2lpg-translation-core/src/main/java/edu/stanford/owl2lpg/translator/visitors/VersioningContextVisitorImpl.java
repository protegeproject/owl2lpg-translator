package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.VersioningContext;
import edu.stanford.owl2lpg.model.VersioningContextVisitor;
import edu.stanford.owl2lpg.translator.Translation;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class VersioningContextVisitorImpl implements VersioningContextVisitor<Translation> {

  @Nonnull
  private final VersioningContextNodeFactory nodeFactory;

  @Nonnull
  private final StructuralEdgeFactory edgeFactory;

  @Inject
  public VersioningContextVisitorImpl(@Nonnull VersioningContextNodeFactory nodeFactory,
                                      @Nonnull StructuralEdgeFactory edgeFactory) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Override
  public Translation visit(VersioningContext versioningContext) {
    var projectId = versioningContext.getProjectId();
    var projectNode = nodeFactory.createProjectNode(projectId);
    var branchId = versioningContext.getBranchId();
    var branchNode = nodeFactory.createBranchNode(branchId);
    var ontoDocId = versioningContext.getOntologyDocumentId();
    var ontoDocNode = nodeFactory.createOntologyDocumentNode(ontoDocId);
    return Translation.create(projectId,
        projectNode,
        ImmutableList.of(edgeFactory.getBranchEdge(projectNode, branchNode)),
        ImmutableList.of(Translation.create(branchId,
            branchNode,
            ImmutableList.of(edgeFactory.getOntologyDocumentEdge(branchNode, ontoDocNode)),
            ImmutableList.of(Translation.create(ontoDocId,
                ontoDocNode,
                ImmutableList.of(),
                ImmutableList.of())))));
  }
}
