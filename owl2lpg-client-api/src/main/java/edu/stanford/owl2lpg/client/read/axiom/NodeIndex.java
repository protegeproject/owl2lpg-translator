package edu.stanford.owl2lpg.client.read.axiom;

import com.google.common.collect.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class NodeIndex {

  @Nonnull
  private final ImmutableSet<Node> startNodes;

  @Nonnull
  private final ImmutableMap<Node, Multimap<String, Node>> indexMap;

  public NodeIndex(@Nonnull ImmutableSet<Node> startNodes,
                   @Nonnull ImmutableMap<Node, Multimap<String, Node>> indexMap) {
    this.startNodes = checkNotNull(startNodes);
    this.indexMap = checkNotNull(indexMap);
  }

  public Collection<Node> getStartNodes() {
    return startNodes;
  }

  @Nullable
  public Node getEndNode(Node startNode, String relLabel) {
    var toNodes = getEndNodes(startNode, relLabel);
    if (toNodes == null) {
      return null;
    }
    return toNodes.stream().findFirst().orElse(null);
  }

  @Nullable
  Collection<Node> getEndNodes(Node startNode, String relLabel) {
    var nestedMap = indexMap.get(startNode);
    if (nestedMap == null) {
      return null;
    }
    return nestedMap.get(relLabel);
  }

  public static class Builder {

    @Nonnull
    private final Set<Node> startNodes;

    private final Map<Node, SetMultimap<String, Node>> indexMap = Maps.newHashMap();

    public Builder(@Nonnull Set<Node> startNodes) {
      this.startNodes = checkNotNull(startNodes);
    }

    public Builder add(Path.Segment segment) {
      var startNode = segment.start();
      var relLabel = segment.relationship().type();
      var endNode = segment.end();

      var innerMap = indexMap.get(startNode);
      if (innerMap == null) {
        innerMap = HashMultimap.create();
        indexMap.put(startNode, innerMap);
      }
      innerMap.put(relLabel, endNode);

      return this;
    }

    public NodeIndex build() {
      return new NodeIndex(
          ImmutableSet.copyOf(startNodes),
          ImmutableMap.copyOf(indexMap));
    }
  }
}
