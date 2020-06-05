package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectPropertyExpression")
public abstract class ObjectPropertyExpression<T extends OWLObjectPropertyExpression>
    extends GraphObject
    implements HasToOwlObject<T> {

  @Override
  public T toOwlObject(OWLDataFactory dataFactory) {
    throw new RuntimeException("Child class must implement this method");
  }
}
