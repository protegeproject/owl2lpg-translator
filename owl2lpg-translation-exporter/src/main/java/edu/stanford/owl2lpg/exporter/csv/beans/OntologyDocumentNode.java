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
public class OntologyDocumentNode {

  @Nonnull
  @CsvBindByName(column = ":ID", required = true)
  private final String nodeId;

  @Nonnull
  @CsvBindByName(column = "ontologyDocumentId:string", required = true)
  private final String ontologyDocumentId;

  @Nonnull
  @CsvBindByName(column = ":LABEL", required = true)
  private final ImmutableList<String> nodeLabels;

  private OntologyDocumentNode(@Nonnull String nodeId,
                               @Nonnull String ontologyDocumentId,
                               @Nonnull ImmutableList<String> nodeLabels) {
    this.nodeId = checkNotNull(nodeId);
    this.ontologyDocumentId = checkNotNull(ontologyDocumentId);
    this.nodeLabels = checkNotNull(nodeLabels);
  }

  public static OntologyDocumentNode of(@Nonnull Node node) {
    return new OntologyDocumentNode(
        node.getNodeId().toString(),
        node.getProperties().get(PropertyFields.ONTOLOGY_DOCUMENT_ID),
        node.getLabels());
  }

  @Nonnull
  public String getNodeId() {
    return nodeId;
  }

  @Nonnull
  public String getOntologyDocumentId() {
    return ontologyDocumentId;
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
    OntologyDocumentNode that = (OntologyDocumentNode) o;
    return Objects.equal(nodeId, that.nodeId) &&
        Objects.equal(ontologyDocumentId, that.ontologyDocumentId) &&
        Objects.equal(nodeLabels, that.nodeLabels);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nodeId, ontologyDocumentId, nodeLabels);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("nodeId", nodeId)
        .add("ontologyDocumentId", ontologyDocumentId)
        .add("nodeLabels", nodeLabels)
        .toString();
  }
}
