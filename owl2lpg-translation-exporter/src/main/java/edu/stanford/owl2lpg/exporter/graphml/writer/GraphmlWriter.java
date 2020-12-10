package edu.stanford.owl2lpg.exporter.graphml.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

public class GraphmlWriter<T> {

  @Nonnull
  private final Writer output;

  @Nonnull
  private Neo4jGraphmlSchema schema;

  @Nonnull
  private final CsvMapper csvMapper;

  private boolean writtenHeader = false;

  private SequenceWriter objectWriter;

  @Inject
  public GraphmlWriter(@Nonnull CsvMapper csvMapper,
                       @Nonnull Neo4jGraphmlSchema schema,
                       @Nonnull Writer output) {
    this.csvMapper = checkNotNull(csvMapper);
    this.output = checkNotNull(output);
    this.schema = checkNotNull(schema);
  }

  public void write(@Nonnull T rowObject) throws IOException {
    if (writtenHeader) {
      writeRow(rowObject);
    } else {
      writeFirstRow(rowObject);
    }
  }

  private void writeFirstRow(@Nonnull T rowObject) throws IOException {
    csvMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    csvMapper.configure(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, false);
    csvMapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
    objectWriter = csvMapper.writer(schema.getGraphmlSchemaWithHeader()).writeValues(output);
    objectWriter.write(rowObject);
    objectWriter.flush();
    objectWriter = csvMapper.writer(schema.getGraphmlSchema()).writeValues(output);
    writtenHeader = true;
  }

  private void writeRow(@Nonnull T rowObject) throws IOException {
    objectWriter.write(rowObject);
    objectWriter.flush();
  }

  public void flush() throws IOException {
    output.flush();
  }
}
