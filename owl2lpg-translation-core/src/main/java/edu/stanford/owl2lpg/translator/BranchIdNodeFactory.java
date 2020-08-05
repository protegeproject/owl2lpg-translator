package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.BranchId;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
public class BranchIdNodeFactory {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Inject
  public BranchIdNodeFactory(@Nonnull NodeFactory nodeFactory) {
    this.nodeFactory = checkNotNull(nodeFactory);
  }

  @Nonnull
  public Node createBranchNode(@Nonnull BranchId branchId) {
    return nodeFactory.createNode(
        branchId,
        NodeLabels.BRANCH,
        Properties.of(BRANCH_ID,
            branchId.getIdentifier()));
  }
}
