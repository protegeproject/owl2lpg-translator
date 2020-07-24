package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ClassDescendantPath {

  public static ClassDescendantPath get(@Nonnull ImmutableList<OWLClass> orderedDescendantList) {
    return new AutoValue_ClassDescendantPath(orderedDescendantList);
  }

  @Nonnull
  public abstract ImmutableList<OWLClass> asOrderedList();

  private int length() {
    return asOrderedList().size();
  }

  @Nonnull
  public OWLClass getBottom() {
    return asOrderedList().get(length() - 1);
  }

  @Nonnull
  public OWLClass getTop() {
    return asOrderedList().get(0);
  }

  @Nonnull
  public OWLClass getDescendantAt(int degree) {
    return asOrderedList().get(degree);
  }

  @Nonnull
  public Stream<OWLClass> getDescendants() {
    return asOrderedList().subList(1, length()).stream();
  }
}
