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
public abstract class ClassAncestorPath extends AncestorPath<OWLClass> {

  public static ClassAncestorPath get(@Nonnull ImmutableList<OWLClass> orderedAncestorList) {
    return new AutoValue_ClassAncestorPath(orderedAncestorList);
  }
}
