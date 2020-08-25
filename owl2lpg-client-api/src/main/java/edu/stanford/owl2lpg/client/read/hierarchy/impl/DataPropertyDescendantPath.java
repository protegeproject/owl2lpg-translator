package edu.stanford.owl2lpg.client.read.hierarchy.impl;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.client.read.hierarchy.DescendantPath;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class DataPropertyDescendantPath extends DescendantPath<OWLDataProperty> {

  @Nonnull
  public static DataPropertyDescendantPath get(@Nonnull ImmutableList<OWLDataProperty> orderedAncestorList) {
    return new AutoValue_DataPropertyDescendantPath(orderedAncestorList);
  }

  @Nonnull
  public abstract ImmutableList<OWLDataProperty> asOrderedList();
}
