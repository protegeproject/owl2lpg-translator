package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;

public class N4jNodeCsvSchema {

    @Nonnull
    public CsvSchema getCsvSchema() {
        return getBuilder().build();
    }

    @Nonnull
    public CsvSchema getCsvSchemaWithHeader() {
        return getBuilder().setUseHeader(true).build();
    }

    private static CsvSchema.Builder getBuilder() {
        return CsvSchema.builder()
                        .addColumn(":ID")
                        .addColumn(":LABEL")
                        .addColumn("projectId")
                        .addColumn("branchId")
                        .addColumn("ontologyDocumentId")
                        .addColumn("projectId")
                        .addColumn("iri")
                        .addColumn("lexicalForm")
                        .addColumn("datatype")
                        .addColumn("language")
                        .addColumn("lang")
                        .addColumn("nodeID")
                        .addColumn("cardinality");
    }
}
