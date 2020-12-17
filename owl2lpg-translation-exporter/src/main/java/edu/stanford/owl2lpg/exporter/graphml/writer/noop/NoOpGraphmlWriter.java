package edu.stanford.owl2lpg.exporter.graphml.writer.noop;

import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlMapper;
import edu.stanford.owl2lpg.exporter.graphml.writer.GraphmlWriter;
import edu.stanford.owl2lpg.exporter.graphml.writer.Neo4jGraphmlSchema;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import org.apache.tinkerpop.gremlin.structure.Graph;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpGraphmlWriter extends GraphmlWriter {

  public NoOpGraphmlWriter(@Nonnull GraphmlMapper graphmlMapper,
                           @Nonnull Neo4jGraphmlSchema schema,
                           @Nonnull Graph graph,
                           @Nonnull Path output) {
    super(graphmlMapper, schema, graph, output);
  }

  @Override
  public void writeNode(@Nonnull Node node) throws IOException {
    // Discard
  }

  @Override
  public void writeEdge(@Nonnull Edge edge) throws IOException {
    // Discard
  }

  @Override
  public void flush() throws IOException {
    // NO-OP
  }
}
