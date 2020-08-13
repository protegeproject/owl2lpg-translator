package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.OntologyContext;
import edu.stanford.owl2lpg.model.OntologyContextVisitor;
import edu.stanford.owl2lpg.translator.Translation;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyContextVisitorImpl implements OntologyContextVisitor<Translation> {

  @Nonnull
  private final OntologyContextNodeFactory nodeFactory;

  @Nonnull
  private final StructuralEdgeFactory edgeFactory;

  @Inject
  public OntologyContextVisitorImpl(@Nonnull OntologyContextNodeFactory nodeFactory,
                                    @Nonnull StructuralEdgeFactory edgeFactory) {
    this.nodeFactory = checkNotNull(nodeFactory);
    this.edgeFactory = checkNotNull(edgeFactory);
  }

  @Override
  public Translation visit(OntologyContext ontologyContext) {
    var projectId = ontologyContext.getProjectId();
    var projectNode = nodeFactory.createProjectNode(projectId);
    var branchId = ontologyContext.getBranchId();
    var branchNode = nodeFactory.createBranchNode(branchId);
    var ontoDocId = ontologyContext.getOntologyDocumentId();
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
