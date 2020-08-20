package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ObjectPropertyDescendantPath extends DescendantPath<OWLObjectProperty> {

  public static ObjectPropertyDescendantPath get(@Nonnull ImmutableList<OWLObjectProperty> orderedDescendantList) {
    return new AutoValue_ObjectPropertyDescendantPath(orderedDescendantList);
  }
}
