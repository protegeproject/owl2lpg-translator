package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Writer;

public class RelationshipsCsvWriterFactory {

    @Nonnull
    private final CsvMapper csvMapper;

    @Nonnull
    private final Neo4jRelationshipsCsvSchema schema;

    @Inject
    public RelationshipsCsvWriterFactory(@Nonnull CsvMapper csvMapper, @Nonnull Neo4jRelationshipsCsvSchema schema) {
        this.csvMapper = csvMapper;
        this.schema = schema;
    }

    public CsvWriter<Edge> create(@Nonnull Writer writer) {
        if(writer instanceof NoOpWriter) {
            return new NoOpCsvWriter<>(csvMapper, schema, writer);
        }
        return new CsvWriter<>(csvMapper, schema, writer);
    }
}
