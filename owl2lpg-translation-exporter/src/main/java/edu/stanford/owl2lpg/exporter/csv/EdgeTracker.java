package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.Edge;

import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface EdgeTracker {

  boolean contains(Edge edge);

  void add(Edge edge, Consumer<Edge> callback);

  int size();
}
