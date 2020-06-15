package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectHasValue;

import javax.annotation.Nullable;

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

  @Nullable
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Nullable
  public Individual getFiller() {
    return filler;
  }

  @Override
  public OWLObjectHasValue toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (property == null || filler == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLObjectHasValue(
          property.toOwlObject(dataFactory, session),
          filler.toOwlObject(dataFactory, session));
    }
  }

  private ObjectHasValue reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
