package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;

public class GraphFactory {

  public static Node Node(@Nonnull NodeId nodeId,
                          @Nonnull ImmutableList<String> labels,
                          @Nonnull Properties properties) {
    return Node.create(nodeId, labels, properties);
  }

  public static Node Node(@Nonnull NodeId nodeId,
                          @Nonnull ImmutableList<String> labels) {
    return Node(nodeId, labels, Properties.empty());
  }

  public static Node Node(int numberId,
                          @Nonnull ImmutableList<String> labels,
                          @Nonnull Properties properties) {
    return Node(NodeId.create(numberId), labels, properties);
  }

  public static Node Node(int numberId,
                          @Nonnull ImmutableList<String> labels) {
    return Node(NodeId.create(numberId), labels, Properties.empty());
  }

  public static Edge Edge(@Nonnull Node fromNode,
                          @Nonnull Node toNode,
                          @Nonnull EdgeLabel label,
                          @Nonnull Properties properties) {
    return Edge.create(fromNode, toNode, label, properties);
  }

  public static Edge Edge(@Nonnull Node fromNode,
                          @Nonnull Node toNode,
                          @Nonnull EdgeLabel label) {
    return Edge.create(fromNode, toNode, label, Properties.empty());
  }
}
