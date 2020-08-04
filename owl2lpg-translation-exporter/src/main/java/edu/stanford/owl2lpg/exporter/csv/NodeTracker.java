package edu.stanford.owl2lpg.exporter.csv;

import edu.stanford.owl2lpg.model.Node;

import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NodeTracker {

  boolean contains(Node node);

  void add(Node obj, Consumer<Node> callback);

  int size();
}
