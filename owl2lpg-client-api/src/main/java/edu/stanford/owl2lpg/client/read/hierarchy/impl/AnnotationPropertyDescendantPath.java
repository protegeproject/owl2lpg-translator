package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.read.hierarchy.DescendantPath;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AnnotationPropertyDescendantPath extends DescendantPath<OWLAnnotationProperty> {

  @Nonnull
  public static AnnotationPropertyDescendantPath get(@Nonnull ImmutableList<OWLAnnotationProperty> orderedDescendantList) {
    return new AutoValue_AnnotationPropertyDescendantPath(orderedDescendantList);
  }

  @Nonnull
  public abstract ImmutableList<OWLAnnotationProperty> asOrderedList();
}
