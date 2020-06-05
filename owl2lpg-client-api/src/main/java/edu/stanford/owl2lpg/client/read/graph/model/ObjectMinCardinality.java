package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectMinCardinality")
public class ObjectMinCardinality extends ClassExpression<OWLObjectMinCardinality> {

  private int cardinality;

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectMinCardinality() {
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
  public OWLObjectMinCardinality toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectMinCardinality(
        cardinality,
        property.toOwlObject(dataFactory),
        filler.toOwlObject(dataFactory));
  }
}
