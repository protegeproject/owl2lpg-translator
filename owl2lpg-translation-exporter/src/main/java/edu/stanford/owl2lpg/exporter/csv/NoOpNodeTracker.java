package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.Node;

import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpNodeTracker implements ExportTracker<Node> {

  @Override
  public boolean contains(Node node) {
    return false;
  }

  @Override
  public void add(Node node, Consumer<Node> callback) {
    callback.accept(node);
  }

  @Override
  public int size() {
    return 0;
  }
}
