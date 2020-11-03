package edu.stanford.owl2lpg.exporter.csv.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkNotNull;

public class CsvWriter<T> {

  @Nonnull
  private final CsvMapper csvMapper;

  @Nonnull
  private final CsvSchema<T> csvSchema;

  @Nonnull
  private final Writer output;

  private final boolean shouldWriteHeader;

  private SequenceWriter objectWriter;

  @Inject
  public CsvWriter(@Nonnull CsvMapper csvMapper,
                   @Nonnull CsvSchema<T> csvSchema,
                   @Nonnull Writer output,
                   boolean shouldWriteHeader) {
    this.csvMapper = checkNotNull(csvMapper);
    this.csvSchema = checkNotNull(csvSchema);
    this.output = checkNotNull(output);
    this.shouldWriteHeader = shouldWriteHeader;
    initialize();
  }

  private void initialize() {
    csvMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    csvMapper.configure(JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, false);
    writeCsvHeader();
  }

  private void writeCsvHeader() {
    try {
      if (shouldWriteHeader) {
        objectWriter = csvMapper.writer(csvSchema.getCsvSchemaWithHeader()).writeValues(output);
        writeHeader();
      } else {
        objectWriter = csvMapper.writer(csvSchema.getCsvSchema()).writeValues(output);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeHeader() throws IOException {
    objectWriter.write(Collections.emptyList());
    flush();
  }

  public void write(@Nonnull T rowObject) throws IOException {
    objectWriter.write(rowObject);
  }

  public boolean isCompatible(T object) {
    return csvSchema.isCompatible(object);
  }

  public void flush() throws IOException {
    output.flush();
  }
}
