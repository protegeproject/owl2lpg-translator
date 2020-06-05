package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectInverseOf")
public class ObjectInverseOf extends ObjectPropertyExpression<OWLObjectInverseOf> {

  @Relationship(type = "OBJECT_PROPERTY")
  private ObjectPropertyExpression property;

  private ObjectInverseOf() {
  }

  @Nonnull
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Override
  public OWLObjectInverseOf toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectInverseOf(property.toOwlObject(dataFactory));
  }
}
