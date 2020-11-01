package edu.stanford.owl2lpg.exporter.csv.writer;

import edu.stanford.owl2lpg.model.Edge;

import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface EdgeTracker {

  /**
   * Performs the callback function when the tracker doesn't contain
   * the given edge.
   *
   * @param edge     The edge to check
   * @param callback A callback function when the cache doesn't contain
   *                 the given edge.
   */
  void add(Edge edge, Consumer<Edge> callback);

  int size();
}
