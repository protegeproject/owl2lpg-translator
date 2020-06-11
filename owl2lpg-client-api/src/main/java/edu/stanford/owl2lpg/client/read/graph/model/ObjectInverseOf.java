package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;

import javax.annotation.Nullable;

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

  @Nullable
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Override
  public OWLObjectInverseOf toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLObjectInverseOf(property.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 1);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
