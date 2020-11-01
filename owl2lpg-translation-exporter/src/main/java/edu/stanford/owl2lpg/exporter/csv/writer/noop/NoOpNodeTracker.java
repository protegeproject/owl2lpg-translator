package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import edu.stanford.owl2lpg.exporter.csv.writer.NodeTracker;
import edu.stanford.owl2lpg.model.Node;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpNodeTracker implements NodeTracker {

  @Inject
  public NoOpNodeTracker() {
  }

  @Override
  public void add(Node node, Consumer<Node> callback) {
    // NO-OP
  }

  @Override
  public int size() {
    return 0;
  }
}
