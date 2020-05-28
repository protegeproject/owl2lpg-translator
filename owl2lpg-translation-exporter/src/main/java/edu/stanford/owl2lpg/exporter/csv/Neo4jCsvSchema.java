package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;

public interface Neo4jCsvSchema {
    @Nonnull
    CsvSchema getCsvSchema();

    @Nonnull
    CsvSchema getCsvSchemaWithHeader();
}
