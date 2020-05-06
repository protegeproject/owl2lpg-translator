package edu.stanford.owl2lpg.exporter.csv.beans;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.opencsv.bean.CsvBindByName;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.versioning.translator.AxiomTranslatorEx.PropertyFields;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ProjectNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "projectId:string", required = true)
  private final String projectId;

  @Nonnull
  @CsvBindByName(column = ":LABEL", required = true)
  private final ImmutableList<String> nodeLabels;

  private ProjectNode(@Nonnull String nodeId,
                      @Nonnull String projectId,
                      @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.projectId = checkNotNull(projectId);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static ProjectNode of(@Nonnull Node node) {
    return new ProjectNode(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.PROJECT_ID),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getProjectId() {
    return projectId;
  }

  @Nonnull
  public ImmutableList<String> getNodeLabels() {
    return nodeLabels;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProjectNode that = (ProjectNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(projectId, that.projectId) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, projectId, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("projectId", projectId)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
