package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class AnnotationPropertyAncestorPath extends AncestorPath<OWLAnnotationProperty> {

  public static AnnotationPropertyAncestorPath get(@Nonnull ImmutableList<OWLAnnotationProperty> orderedAncestorList) {
    return new AutoValue_AnnotationPropertyAncestorPath(orderedAncestorList);
  }
}
