package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;

import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.*;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.CARDINALITY;

public class Neo4jRelationshipsCsvSchema implements Neo4jCsvSchema {

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
                        .addColumn(":LABEL")
                        .addColumn("START_ID")
                        .addColumn("END_ID")
                        .addColumn(TYPE)
                        .addColumn(IRI);
    }
}
