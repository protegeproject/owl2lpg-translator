package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeFactory;
import edu.stanford.owl2lpg.model.ProjectId;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectIdNodeFactory {

  @Nonnull
  private final NodeFactory nodeFactory;

  @Inject
  public ProjectIdNodeFactory(@Nonnull NodeFactory nodeFactory) {
    this.nodeFactory = checkNotNull(nodeFactory);
  }

  @Nonnull
  public Node createProjectNode(@Nonnull ProjectId projectId) {
    return nodeFactory.createNode(
        projectId,
        NodeLabels.PROJECT,
        Properties.of(PROJECT_ID,
            projectId.getIdentifier()));
  }
}
