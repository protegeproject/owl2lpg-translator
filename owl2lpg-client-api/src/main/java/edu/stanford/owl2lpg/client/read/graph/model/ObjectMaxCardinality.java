package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectMaxCardinality")
public class ObjectMaxCardinality extends ClassExpression<OWLObjectMaxCardinality> {

  private int cardinality;

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectMaxCardinality() {
  }

  public int getCardinality() {
    return cardinality;
  }

  @Nonnull
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Nonnull
  public ClassExpression getFiller() {
    return filler;
  }

  @Override
  public OWLObjectMaxCardinality toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectMaxCardinality(
        cardinality,
        property.toOwlObject(dataFactory),
        filler.toOwlObject(dataFactory));
  }
}
