package edu.stanford.owl2lpg.client.read.hierarchy;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class ClassDescendantPath extends DescendantPath<OWLClass> {

  public static ClassDescendantPath get(@Nonnull ImmutableList<OWLClass> orderedDescendantList) {
    return new AutoValue_ClassDescendantPath(orderedDescendantList);
  }
}
