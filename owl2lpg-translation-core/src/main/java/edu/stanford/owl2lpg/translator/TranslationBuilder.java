package edu.stanford.owl2lpg.translator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.Properties;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.stanford.owl2lpg.model.GraphFactory.Edge;

public class TranslationBuilder {

  private final Map<Node, Set<Edge>> nodeConnections = Maps.newHashMap();
  private final Map<Edge, Node> sourceNodes = Maps.newHashMap();
  private final Map<Edge, Node> targetNodes = Maps.newHashMap();

  public TranslationBuilder() {
    // NO-OP
  }

  public TranslationBuilder add(@Nonnull Node sourceNode,
                                @Nonnull Node targetNode,
                                @Nonnull EdgeLabel edgeLabel,
                                @Nonnull Properties edgeProperties) {
    var edge = Edge(sourceNode, targetNode, edgeLabel, edgeProperties);
    addNodeConnection(sourceNode, edge);
    addSourceNode(edge, sourceNode);
    addTargetNode(edge, targetNode);
    return this;
  }

  public TranslationBuilder add(@Nonnull Node fromNode,
                                @Nonnull Node toNode,
                                @Nonnull EdgeLabel edgeLabel) {
    return add(fromNode, toNode, edgeLabel, Properties.empty());
  }

  public Translation build() {
    var mainNode = getMainNode();
    return createTranslation(mainNode);
  }

  private void addNodeConnection(Node node, Edge edge) {
    nodeConnections.putIfAbsent(node, Sets.newHashSet());
    nodeConnections.get(node).add(edge);
  }

  private void addSourceNode(Edge edge, Node sourceNode) {
    sourceNodes.put(edge, sourceNode);
  }

  private void addTargetNode(Edge edge, Node targetNode) {
    targetNodes.put(edge, targetNode);
  }

  private Translation createTranslation(Node mainNode) {
    var edges = nodeConnections.get(mainNode);
    if (edges == null) {
      return Translation.create(mainNode);
    } else {
      var nestedTranslations = edges.stream()
          .map(targetNodes::get)
          .map(this::createTranslation)
          .collect(Collectors.toList());
      return Translation.create(mainNode,
          ImmutableList.copyOf(edges),
          ImmutableList.copyOf(nestedTranslations));
    }
  }

  private Node getMainNode() {
    var parentNodes = Sets.newHashSet(sourceNodes.values());
    var childNodes = Sets.newHashSet(targetNodes.values());
    var diff = Sets.difference(parentNodes, childNodes);
    if (diff.size() == 0) {
      throw new RuntimeException("Translation has no main node");
    } else if (diff.size() > 1) {
      throw new RuntimeException("Translation has multiple main nodes");
    }
    return diff.stream().findFirst().get();
  }
}
