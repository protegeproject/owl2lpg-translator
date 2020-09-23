package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Node.N4J_JSON_ID;
import static edu.stanford.owl2lpg.model.Node.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.HASH_CODE;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.LANGUAGE;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.LEXICAL_FORM;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.LOCAL_NAME;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.NODE_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.OBO_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PREFIXED_NAME;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;

public class Neo4jNodeCsvSchema implements Neo4jCsvSchema {

  @Inject
  public Neo4jNodeCsvSchema() {
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

  private static CsvSchema.Builder getBuilder() {
    return CsvSchema.builder()
        .addColumn(N4J_JSON_ID)
        .addColumn(N4J_JSON_LABELS)
        .addColumn(PROJECT_ID)
        .addColumn(BRANCH_ID)
        .addColumn(ONTOLOGY_DOCUMENT_ID)
        .addColumn(IRI)
        .addColumn(LOCAL_NAME)
        .addColumn(PREFIXED_NAME)
        .addColumn(OBO_ID)
        .addColumn(LEXICAL_FORM)
        .addColumn(DATATYPE)
        .addColumn(LANGUAGE)
        .addColumn(NODE_ID)
        .addColumn(CARDINALITY + ":int")
        .addColumn(HASH_CODE);
  }
}
