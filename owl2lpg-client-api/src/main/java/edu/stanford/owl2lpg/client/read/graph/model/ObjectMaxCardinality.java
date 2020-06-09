package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Required;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectMaxCardinality")
public class ObjectMaxCardinality extends ClassExpression<OWLObjectMaxCardinality> {

  @Property
  @Required
  private Integer cardinality;

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectMaxCardinality() {
  }

  @Nullable
  public Integer getCardinality() {
    return cardinality;
  }

  @Nullable
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Nullable
  public ClassExpression getFiller() {
    return filler;
  }

  @Override
  public OWLObjectMaxCardinality toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLObjectMaxCardinality(
          getCardinality(),
          property.toOwlObject(dataFactory, session),
          filler.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 1);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
