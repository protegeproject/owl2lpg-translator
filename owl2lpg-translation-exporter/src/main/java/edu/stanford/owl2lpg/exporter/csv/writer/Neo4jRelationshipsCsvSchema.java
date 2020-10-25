package edu.stanford.owl2lpg.exporter.csv.writer;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_END_ID;
import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_START_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.POS;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.STRUCTURAL_SPEC;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.TYPE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
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
        .addColumn(IRI)
        .addColumn(STRUCTURAL_SPEC + ":boolean")
        .addColumn(POS + ":int");
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
