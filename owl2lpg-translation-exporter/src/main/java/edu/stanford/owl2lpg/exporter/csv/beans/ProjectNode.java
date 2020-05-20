package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx.PropertyFields;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ProjectNode {

  public static ProjectNode create(@Nonnull String nodeId,
                                   @Nonnull String projectId,
                                   @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_ProjectNode(nodeId, projectId, nodeLabels);
  }

  public static ProjectNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.PROJECT_ID),
        node.getLabels());
  }

  @Nonnull
  public abstract String getNodeId();

  @Nonnull
  public abstract String getProjectId();

  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
