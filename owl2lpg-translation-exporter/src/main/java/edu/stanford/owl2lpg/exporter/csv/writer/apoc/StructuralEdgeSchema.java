package edu.stanford.owl2lpg.exporter.csv.writer.apoc;

import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_END_ID;
import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_START_ID;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.NEXT;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StructuralEdgeSchema implements CsvSchema<Edge> {

  @Inject
  public StructuralEdgeSchema() {
  }

  @Nonnull
  @Override
  public com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchema() {
    return getBuilder().build();
  }

  @Nonnull
  @Override
  public com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchemaWithHeader() {
    return getBuilder().setUseHeader(true).build();
  }

  @Override
  public boolean isCompatible(Edge edge) {
    return edge.isStructuralEdge() && !edge.isTypeOf(NEXT);
  }

  private static com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder getBuilder() {
    return com.fasterxml.jackson.dataformat.csv.CsvSchema.builder()
        .addColumn(N4J_JSON_LABELS)
        .addColumn(N4J_JSON_START_ID)
        .addColumn(N4J_JSON_END_ID);
  }
}
