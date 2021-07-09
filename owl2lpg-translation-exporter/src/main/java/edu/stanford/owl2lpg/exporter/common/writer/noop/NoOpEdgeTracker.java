package edu.stanford.owl2lpg.exporter.common.writer.noop;

import edu.stanford.owl2lpg.exporter.common.writer.EdgeTracker;
import edu.stanford.owl2lpg.model.Edge;

import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpEdgeTracker implements EdgeTracker {

  @Override
  public boolean contains(Edge edge) {
    return false;
  }

  @Override
  public void add(Edge edge, Consumer<Edge> callback) {
    callback.accept(edge);
  }

  @Override
  public int size() {
    return 0;
  }
}
