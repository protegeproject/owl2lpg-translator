package edu.stanford.owl2lpg.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class GraphFactory {

  public static Node Node(@Nonnull ImmutableList<String> labels,
                          @Nonnull Properties properties,
                          @Nonnull NodeIdProvider nodeIdProvider) {
    return Node.create(nodeIdProvider.getId(), labels, properties);
  }

  public static Node Node(@Nonnull ImmutableList<String> labels,
                          @Nonnull NodeIdProvider nodeIdProvider) {
    return Node.create(nodeIdProvider.getId(), labels, Properties.empty());
  }

  public static NodeIdProvider withIdentifierFrom(@Nonnull Object obj) {
    return new HashCodeGetter(obj);
  }

  public static Edge Edge(@Nonnull Node fromNode,
                          @Nonnull Node toNode,
                          @Nonnull String label,
                          @Nonnull Properties properties,
                          boolean isBidirectional) {
    return Edge.create(fromNode, toNode, label, properties, isBidirectional);
  }

  public static Edge Edge(@Nonnull Node fromNode,
                          @Nonnull Node toNode,
                          @Nonnull String label,
                          boolean isBidirectional) {
    return Edge.create(fromNode, toNode, label, Properties.empty(), isBidirectional);
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

  static class HashCodeGetter implements NodeIdProvider {

    @Nonnull
    private final Object obj;

    HashCodeGetter(@Nonnull Object obj) {
      this.obj = checkNotNull(obj);
    }

    @Override
    public int getId() {
      return obj.hashCode() & 0xfffffff;
    }
  }
}
