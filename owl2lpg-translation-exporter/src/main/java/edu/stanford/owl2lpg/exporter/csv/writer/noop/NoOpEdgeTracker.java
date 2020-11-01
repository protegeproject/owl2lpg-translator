package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import edu.stanford.owl2lpg.exporter.csv.writer.EdgeTracker;
import edu.stanford.owl2lpg.model.Edge;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpEdgeTracker implements EdgeTracker {

  @Inject
  public NoOpEdgeTracker() {
  }

  @Override
  public void add(Edge edge, Consumer<Edge> callback) {
    // NO-OP
  }

  @Override
  public int size() {
    return 0;
  }
}
