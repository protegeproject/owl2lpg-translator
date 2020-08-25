package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.read.hierarchy.DescendantPath;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ClassDescendantPath extends DescendantPath<OWLClass> {

  @Nonnull
  public static ClassDescendantPath get(@Nonnull ImmutableList<OWLClass> orderedDescendantList) {
    return new AutoValue_ClassDescendantPath(orderedDescendantList);
  }

  @Nonnull
  public abstract ImmutableList<OWLClass> asOrderedList();
}
