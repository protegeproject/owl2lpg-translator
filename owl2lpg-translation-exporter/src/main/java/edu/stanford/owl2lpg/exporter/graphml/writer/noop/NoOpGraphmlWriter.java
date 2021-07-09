package edu.stanford.owl2lpg.exporter.graphml.writer.noop;

import edu.stanford.owl2lpg.exporter.graphml.writer.GraphmlWriter;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpGraphmlWriter extends GraphmlWriter {

  public NoOpGraphmlWriter(@Nonnull TinkerGraph graph,
                           @Nonnull Path output) {
    super(graph, output);
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
