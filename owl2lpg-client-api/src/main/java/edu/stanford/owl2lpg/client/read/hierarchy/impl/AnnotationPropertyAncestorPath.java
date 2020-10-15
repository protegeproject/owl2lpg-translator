package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.read.hierarchy.AncestorPath;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AnnotationPropertyAncestorPath extends AncestorPath<OWLAnnotationProperty> {

  @Nonnull
  public static AnnotationPropertyAncestorPath get(@Nonnull ImmutableList<OWLAnnotationProperty> orderedAncestorList) {
    return new AutoValue_AnnotationPropertyAncestorPath(orderedAncestorList);
  }

  @Nonnull
  public abstract ImmutableList<OWLAnnotationProperty> asOrderedList();
}
