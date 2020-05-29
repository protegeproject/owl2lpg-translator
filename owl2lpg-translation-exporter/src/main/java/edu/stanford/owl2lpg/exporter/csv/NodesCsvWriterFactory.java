package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Writer;

public class NodesCsvWriterFactory {

    @Nonnull
    private final CsvMapper csvMapper;

    @Nonnull
    private final Neo4jNodeCsvSchema schema;

    @Inject
    public NodesCsvWriterFactory(@Nonnull CsvMapper csvMapper, @Nonnull Neo4jNodeCsvSchema schema) {
        this.csvMapper = csvMapper;
        this.schema = schema;
    }

    @Nonnull
    public CsvWriter<Node> create(@Nonnull Writer writer) {
        if(writer instanceof NoOpWriter) {
            return new NoOpCsvWriter<>(csvMapper, schema, writer);
        }
        return new CsvWriter<>(csvMapper, schema, writer);
    }
}
