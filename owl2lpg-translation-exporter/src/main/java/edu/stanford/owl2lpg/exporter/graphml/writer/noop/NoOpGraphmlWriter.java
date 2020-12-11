package edu.stanford.owl2lpg.exporter.graphml.writer.noop;

import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlMapper;
import edu.stanford.owl2lpg.exporter.graphml.writer.GraphmlWriter;
import edu.stanford.owl2lpg.exporter.graphml.writer.Neo4jGraphmlSchema;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpGraphmlWriter<T> extends GraphmlWriter<T> {

  public NoOpGraphmlWriter(@Nonnull GraphmlMapper graphmlMapper,
                           @Nonnull Neo4jGraphmlSchema schema,
                           @Nonnull Writer output) {
    super(graphmlMapper, schema, output);
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
