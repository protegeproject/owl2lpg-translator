package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AnnotationPropertyDescendantPath extends DescendantPath<OWLAnnotationProperty> {

  public static AnnotationPropertyDescendantPath get(@Nonnull ImmutableList<OWLAnnotationProperty> orderedDescendantList) {
    return new AutoValue_AnnotationPropertyDescendantPath(orderedDescendantList);
  }
}
