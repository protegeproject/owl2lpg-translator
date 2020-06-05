package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectHasValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectHasValue")
public class ObjectHasValue extends ClassExpression<OWLObjectHasValue> {

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "INDIVIDUAL")
  private Individual filler;

  private ObjectHasValue() {
  }

  @Nonnull
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Nonnull
  public Individual getFiller() {
    return filler;
  }

  @Override
  public OWLObjectHasValue toOwlObject(OWLDataFactory dataFactory) {
    if (property == null) System.out.println(filler.toOwlObject(dataFactory));
    return dataFactory.getOWLObjectHasValue(
        property.toOwlObject(dataFactory),
        filler.toOwlObject(dataFactory));
  }
}
