package edu.stanford.owl2lpg.datastructure;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a graph node that has a list of edges. When defining the edges,
 * the code assumes the graph will have only one root node, called the main node.
 * If a graph node becomes part of a larger graph construction, we can consider
 * the node as a sub-graph of it.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Graph extends AnyNode {

  private final List<Edge> edges;

  private Graph(@Nonnull List<Edge> edges) {
    this.edges = checkNotNull(edges);
  }

  public static Graph create(Edge... edges) {
    return new Graph(Lists.newArrayList(edges));
  }

  public static Graph create(List<Edge> edges) {
    return new Graph(edges);
  }

  public Node getMainNode() {
    return edges.stream().findFirst().get().getFromNode();
  }

  public List<Edge> getEdges() {
    return edges;
  }

  @Override
  public boolean isNode() {
    return false;
  }

  @Override
  public boolean isGraph() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Graph graph = (Graph) o;
    return Objects.equal(edges, graph.edges);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(edges);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("edges", edges)
        .toString();
  }
}
