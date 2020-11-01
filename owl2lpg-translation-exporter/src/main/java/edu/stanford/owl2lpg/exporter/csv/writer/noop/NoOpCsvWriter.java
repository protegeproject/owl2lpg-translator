package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpCsvWriter<T> extends CsvWriter<T> {

  public NoOpCsvWriter(@Nonnull CsvMapper csvMapper,
                       @Nonnull CsvSchema schema,
                       @Nonnull Writer output) {
    super(csvMapper, schema, output);
  }

  @Override
  public void write(@Nonnull T rowObject) throws IOException {
    // Discard
  }

  @Override
  public void flush() throws IOException {
    // NO-OP
  }
}
