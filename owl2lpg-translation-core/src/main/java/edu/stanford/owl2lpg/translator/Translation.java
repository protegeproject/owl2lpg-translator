package edu.stanford.owl2lpg.translator;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Translation {

  public static Translation create(@Nonnull Node mainNode,
                                   @Nonnull ImmutableList<Edge> edges,
                                   @Nonnull ImmutableList<Translation> nestedTranslations) {
    return new AutoValue_Translation(mainNode, edges, nestedTranslations);
  }

  public static Translation create(@Nonnull Node mainNode,
                                   @Nonnull ImmutableList<Edge> edges) {
    return Translation.create(mainNode, edges, ImmutableList.of());
  }

  public static Translation create(@Nonnull Node mainNode) {
    return Translation.create(mainNode, ImmutableList.of(), ImmutableList.of());
  }

  public abstract Node getMainNode();

  public abstract ImmutableList<Edge> getEdges();

  public abstract ImmutableList<Translation> getNestedTranslations();

  /**
   * Gets all the nodes with the given node label.
   *
   * @param label The node label to filter.
   * @return A collection of nodes.
   */
  public Stream<Node> nodes(String label) {
    return nodes().filter(node -> node.getLabels().contains(label));
  }

  /**
   * Gets all the edges with the given edge label.
   *
   * @param label The edge label to filter.
   * @return A collection of edges.
   */
  public Stream<Edge> edges(String label) {
    return edges().filter(edge -> edge.getLabel().contains(label));
  }

  /**
   * Gets all the nodes that make up this translation, including from
   * its nested translation.
   *
   * @return a stream of nodes.
   */
  public Stream<Node> nodes() {
    var s1 = Stream.of(getMainNode());
    var s2 = getNestedTranslations().stream().flatMap(Translation::nodes);
    return Stream.concat(s1, s2);
  }

  /**
   * Gets all the edges that make up this translation, including from
   * its nested translation.
   *
   * @return a stream of edges.
   */
  public Stream<Edge> edges() {
    var s1 = getEdges().stream();
    var s2 = getNestedTranslations().stream().flatMap(Translation::edges);
    return Stream.concat(s1, s2);
  }
}
