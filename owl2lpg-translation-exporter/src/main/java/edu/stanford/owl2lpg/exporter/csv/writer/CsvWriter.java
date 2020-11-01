package edu.stanford.owl2lpg.exporter.csv.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;

import static com.google.common.base.Preconditions.checkNotNull;

public class CsvWriter<T> {

  @Nonnull
  private final CsvMapper csvMapper;

  @Nonnull
  private CsvSchema csvSchema;

  @Nonnull
  private final Writer output;

  private boolean writtenHeader = false;

  private SequenceWriter objectWriter;

  @Inject
  public CsvWriter(@Nonnull CsvMapper csvMapper,
                   @Nonnull CsvSchema csvSchema,
                   @Nonnull Writer output) {
    this.csvMapper = checkNotNull(csvMapper);
    this.csvSchema = checkNotNull(csvSchema);
    this.output = checkNotNull(output);
  }

  @Nonnull
  public void write(@Nonnull T rowObject) throws IOException {
    if (writtenHeader) {
      writeRow(rowObject);
    } else {
      writeFirstRow(rowObject);
    }
  }

  public boolean isCompatible(T object) {
    return csvSchema.isCompatible(object);
  }

  private void writeFirstRow(@Nonnull T rowObject) {
    try {
      csvMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
      csvMapper.configure(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, false);
      csvMapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
      objectWriter = csvMapper.writer(csvSchema.getCsvSchemaWithHeader()).writeValues(output);
      objectWriter.write(rowObject);
      objectWriter.flush();
      objectWriter = csvMapper.writer(csvSchema.getCsvSchema()).writeValues(output);
      writtenHeader = true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeRow(@Nonnull T rowObject) throws IOException {
    objectWriter.write(rowObject);
    objectWriter.flush();
  }

  public void flush() throws IOException {
    output.flush();
  }
}
