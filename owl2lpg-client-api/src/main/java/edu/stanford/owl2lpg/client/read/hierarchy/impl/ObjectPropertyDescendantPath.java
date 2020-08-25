package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.read.hierarchy.DescendantPath;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ObjectPropertyDescendantPath extends DescendantPath<OWLObjectProperty> {

  @Nonnull
  public static ObjectPropertyDescendantPath get(@Nonnull ImmutableList<OWLObjectProperty> orderedDescendantList) {
    return new AutoValue_ObjectPropertyDescendantPath(orderedDescendantList);
  }

  @Nonnull
  public abstract ImmutableList<OWLObjectProperty> asOrderedList();
}
