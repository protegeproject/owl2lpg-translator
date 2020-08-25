package edu.stanford.owl2lpg.client.read.axiom.impl;

import com.google.common.collect.*;
import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class NodeIndexImpl implements NodeIndex {

  @Nonnull
  private final ImmutableSetMultimap<String, Node> nodeMap;

  @Nonnull
  private final ImmutableMap<Node, Multimap<String, Node>> segmentMap;

  public NodeIndexImpl(@Nonnull ImmutableSetMultimap<String, Node> nodeMap,
                       @Nonnull ImmutableMap<Node, Multimap<String, Node>> segmentMap) {
    this.nodeMap = checkNotNull(nodeMap);
    this.segmentMap = checkNotNull(segmentMap);
  }

  @Override
  public Collection<Node> getNodes(String label) {
    return nodeMap.get(label);
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
  public Collection<Node> getEndNodes(Node startNode, String relLabel) {
    var nestedMap = segmentMap.get(startNode);
    if (nestedMap == null) {
      return null;
    }
    return nestedMap.get(relLabel);
  }

  public static class Builder {

    private final Multimap<String, Node> nodeMap = HashMultimap.create();

    private final Map<Node, SetMultimap<String, Node>> segmentMap = Maps.newHashMap();

    public Builder() {
    }

    public Builder add(Path.Segment segment) {
      var startNode = segment.start();
      var edgeLabel = segment.relationship().type();
      var endNode = segment.end();

      buildNodeMap(startNode);
      buildSegmentMap(startNode, edgeLabel, endNode);

      return this;
    }

    private void buildNodeMap(Node node) {
      node.labels().forEach(label -> nodeMap.put(label, node));
    }

    private void buildSegmentMap(Node startNode, String relLabel, Node endNode) {
      var innerMap = segmentMap.get(startNode);
      if (innerMap == null) {
        innerMap = HashMultimap.create();
        segmentMap.put(startNode, innerMap);
      }
      innerMap.put(relLabel, endNode);
    }

    public NodeIndexImpl build() {
      return new NodeIndexImpl(
          ImmutableSetMultimap.copyOf(nodeMap),
          ImmutableMap.copyOf(segmentMap));
    }
  }
}
