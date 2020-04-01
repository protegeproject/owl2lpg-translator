package edu.stanford.owl2lpg.translator;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

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

  public static Node MainNode(Translation translation) {
    return translation.getMainNode();
  }

  public abstract Node getMainNode();

  public abstract ImmutableList<Edge> getEdges();

  public abstract ImmutableList<Translation> getNestedTranslations();

  public Stream<Edge> edges() {
    Stream s1 = getEdges().stream();
    Stream s2 = getNestedTranslations().stream().flatMap(t -> t.getEdges().stream());
    return Stream.concat(s1, s2);
  }

  public Stream<Translation> closure() {
    Stream nestedClosure = this.getNestedTranslations().stream();
    return Stream.concat(Stream.of(this), nestedClosure);
  }
}
