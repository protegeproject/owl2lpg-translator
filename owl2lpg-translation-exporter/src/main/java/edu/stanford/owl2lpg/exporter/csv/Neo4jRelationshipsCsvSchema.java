package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Edge.*;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.TYPE;

public class Neo4jRelationshipsCsvSchema implements Neo4jCsvSchema {

  @Inject
  public Neo4jRelationshipsCsvSchema() {
  }

  private static CsvSchema.Builder getBuilder() {
    return CsvSchema.builder()
        .addColumn(N4J_JSON_LABELS)
        .addColumn(N4J_JSON_START_ID)
        .addColumn(N4J_JSON_END_ID)
        .addColumn(TYPE)
        .addColumn(IRI);
  }

  @Override
  @Nonnull
  public CsvSchema getCsvSchema() {
    return getBuilder().build();
  }

  @Override
  @Nonnull
  public CsvSchema getCsvSchemaWithHeader() {
    return getBuilder().setUseHeader(true).build();
  }
}
