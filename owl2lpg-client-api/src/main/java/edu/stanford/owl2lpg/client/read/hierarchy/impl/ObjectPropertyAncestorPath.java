package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.read.hierarchy.AncestorPath;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ObjectPropertyAncestorPath extends AncestorPath<OWLObjectProperty> {

  @Nonnull
  public static ObjectPropertyAncestorPath get(@Nonnull ImmutableList<OWLObjectProperty> orderedAncestorList) {
    return new AutoValue_ObjectPropertyAncestorPath(orderedAncestorList);
  }

  @Nonnull
  public abstract ImmutableList<OWLObjectProperty> asOrderedList();
}
