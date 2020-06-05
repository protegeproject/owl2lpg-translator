package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectSomeValuesFrom")
public class ObjectSomeValuesFrom extends ClassExpression<OWLObjectSomeValuesFrom> {

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectSomeValuesFrom() {
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
  public OWLObjectSomeValuesFrom toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLObjectSomeValuesFrom(
          property.toOwlObject(dataFactory, session),
          filler.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 2);
      return object.toOwlObject(dataFactory, session);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("property", property)
        .add("filler", filler)
        .toString();
  }
}
