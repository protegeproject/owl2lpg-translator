package edu.stanford.owl2lpg.translator.visitors;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class PropertyChain implements Iterable<OWLObjectPropertyExpression> {

  @Nonnull
  public static PropertyChain create(@Nonnull ImmutableList<OWLObjectPropertyExpression> listOfProperty) {
    return new AutoValue_PropertyChain(listOfProperty);
  }

  @Nonnull
  public abstract ImmutableList<OWLObjectPropertyExpression> asList();

  @Override
  public Iterator<OWLObjectPropertyExpression> iterator() {
    return asList().iterator();
  }
}
