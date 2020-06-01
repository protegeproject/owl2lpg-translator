package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

public class NoOpCsvWriter<T> extends CsvWriter<T> {

    public NoOpCsvWriter(@Nonnull CsvMapper csvMapper, @Nonnull Neo4jCsvSchema schema, @Nonnull Writer output) {
        super(csvMapper, schema, output);
    }

    @Override
    public void write(@Nonnull T rowObject) throws IOException {
        // Discard
    }

    @Override
    public void flush() throws IOException {

    }
}
