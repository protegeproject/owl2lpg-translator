package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.owl2lpg.model.GraphFactory.Edge;

public class TranslationBuilder {

  private final Map<Node, Set<Edge>> nodeConnections = Maps.newHashMap();
  private final Map<Edge, Node> outEdgeMap = Maps.newHashMap();
  private final Map<Edge, Node> inEdgeMap = Maps.newHashMap();

  public TranslationBuilder() {
    // NO-OP
  }

  public TranslationBuilder add(Node fromNode,
                                Node toNode,
                                String edgeLabel,
                                Properties edgeProperties) {
    var edge = Edge(fromNode, toNode, edgeLabel, edgeProperties);
    addConnection(fromNode, edge);
    addOutNode(edge, fromNode);
    addInNode(edge, toNode);
    return this;
  }

  public TranslationBuilder add(Node fromNode,
                                Node toNode,
                                String edgeLabel) {
    return add(fromNode, toNode, edgeLabel, Properties.empty());
  }

  private void addConnection(Node node, Edge edge) {
    nodeConnections.putIfAbsent(node, Sets.newHashSet());
    nodeConnections.get(node).add(edge);
  }

  private void addOutNode(Edge edge, Node fromNode) {
    outEdgeMap.put(edge, fromNode);
  }

  private void addInNode(Edge edge, Node toNode) {
    inEdgeMap.put(edge, toNode);
  }

  public Translation build() {
    var rootNode = getRootNode();
    return createTranslation(rootNode);
  }

  private Translation createTranslation(Node mainNode) {
    var edges = nodeConnections.get(mainNode);
    if (edges == null) {
      return Translation.create(mainNode);
    } else {
      var nestedTranslations = edges.stream()
          .map(edge -> {
            var inNode = inEdgeMap.get(edge);
            return createTranslation(inNode);
          })
          .collect(Collectors.toList());
      return Translation.create(mainNode,
          ImmutableList.copyOf(edges),
          ImmutableList.copyOf(nestedTranslations));
    }
  }

  private Node getRootNode() {
    var parentNodes = Sets.newHashSet(outEdgeMap.values());
    var childNodes = Sets.newHashSet(inEdgeMap.values());
    var diff = Sets.difference(parentNodes, childNodes);
    if (diff.size() != 1) {
      throw new RuntimeException("Translation graph has multiple main nodes");
    }
    return diff.stream().findFirst().get();
  }
}
