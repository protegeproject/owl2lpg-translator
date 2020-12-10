package edu.stanford.owl2lpg.exporter.graphml.writer;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlSchema;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Edge.*;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jRelationshipsGraphmlSchema implements Neo4jGraphmlSchema {

  @Inject
  public Neo4jRelationshipsGraphmlSchema() {
  }

  private static GraphmlSchema.Builder getBuilder() {
    return GraphmlSchema.builder()
        .addColumn(N4J_JSON_LABELS)
        .addColumn(N4J_JSON_START_ID)
        .addColumn(N4J_JSON_END_ID)
        .addColumn(TYPE)
        .addColumn(IRI)
        .addColumn(STRUCTURAL_SPEC + ":boolean")
        .addColumn(POS + ":int");
  }

  @Override
  @Nonnull
  public GraphmlSchema getGraphmlSchema() {
    return getBuilder().build();
  }

  @Override
  @Nonnull
  public GraphmlSchema getGraphmlSchemaWithHeader() {
    return getBuilder().setUseHeader(true).build();
  }
}
