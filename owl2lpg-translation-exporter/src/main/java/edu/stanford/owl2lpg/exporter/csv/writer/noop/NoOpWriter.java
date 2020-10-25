package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpWriter extends Writer {

  @Override
  public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
    // NO-OP
  }

  @Override
  public void flush() throws IOException {
    // NO-OP
  }

  @Override
  public void close() throws IOException {
    // NO-OP
  }
}
