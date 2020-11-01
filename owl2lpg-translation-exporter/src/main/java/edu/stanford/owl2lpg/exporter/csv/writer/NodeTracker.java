package edu.stanford.owl2lpg.exporter.csv.writer;

import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface NodeTracker {

  /**
   * Performs the callback function when the tracker doesn't contain
   * the given node.
   *
   * @param node     The node to check
   * @param callback A callback function when the cache doesn't contain
   *                 the given node.
   */
  void add(@Nonnull Node node, @Nonnull Consumer<Node> callback);

  int size();
}
