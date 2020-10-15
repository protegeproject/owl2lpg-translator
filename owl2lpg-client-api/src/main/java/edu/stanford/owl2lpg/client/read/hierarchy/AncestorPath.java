package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class AncestorPath<E extends OWLEntity> {

  @Nonnull
  public abstract ImmutableList<E> asOrderedList();

  private int length() {
    return asOrderedList().size();
  }

  @Nonnull
  public E getBottom() {
    return asOrderedList().get(0);
  }

  @Nonnull
  public E getTop() {
    return asOrderedList().get(length() - 1);
  }

  @Nonnull
  public E getAncestorAt(int degree) {
    return asOrderedList().get(degree);
  }

  @Nonnull
  public Stream<E> getAncestors() {
    return asOrderedList().subList(1, length()).stream();
  }
}
