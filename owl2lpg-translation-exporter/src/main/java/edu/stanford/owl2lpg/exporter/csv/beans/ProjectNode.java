package edu.stanford.owl2lpg.exporter.csv.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ProjectNode {

  public static final String NODE_ID = ":ID";

  public static final String PROPERTY_PROJECT_ID = "projectId:string";

  public static final String NODE_LABELS = ":LABEL";

  @JsonCreator
  @Nonnull
  public static ProjectNode create(@JsonProperty(NODE_ID) @Nonnull String nodeId,
                                   @JsonProperty(PROPERTY_PROJECT_ID) @Nonnull String projectId,
                                   @JsonProperty(NODE_LABELS) @Nonnull ImmutableList<String> nodeLabels) {
    return new AutoValue_ProjectNode(nodeId, projectId, nodeLabels);
  }

  @Nonnull
  public static ProjectNode of(@Nonnull Node node) {
    checkNotNull(node);
    return create(
        node.printNodeId(),
        node.getProperty(PropertyFields.PROJECT_ID),
        node.getLabels().asList());
  }

  @JsonProperty(NODE_ID)
  @Nonnull
  public abstract String getNodeId();

  @JsonProperty(PROPERTY_PROJECT_ID)
  @Nonnull
  public abstract String getProjectId();

  @JsonProperty(NODE_LABELS)
  @Nonnull
  public abstract ImmutableList<String> getNodeLabels();
}
