package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphFactory {

  private static AtomicInteger nodeIdGenerator = new AtomicInteger(0);

  public static Node Node(@Nonnull ImmutableList<String> labels, @Nonnull Properties properties) {
    return Node.create(nodeIdGenerator.incrementAndGet(), labels, properties);
  }

  public static Node Node(@Nonnull ImmutableList<String> labels) {
    return Node.create(nodeIdGenerator.incrementAndGet(), labels, Properties.empty());
  }

  public static Edge Edge(@Nonnull Node fromNode,
                          @Nonnull Node toNode,
                          @Nonnull String label,
                          @Nonnull Properties properties) {
    return Edge.create(fromNode, toNode, label, properties);
  }

  public static Edge Edge(@Nonnull Node fromNode,
                          @Nonnull Node toNode,
                          @Nonnull String label) {
    return Edge.create(fromNode, toNode, label, Properties.empty());
  }
}
