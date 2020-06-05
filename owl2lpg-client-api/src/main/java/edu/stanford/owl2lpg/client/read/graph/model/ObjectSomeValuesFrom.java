package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;

import javax.annotation.Nonnull;

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

  @Nonnull
  public ObjectPropertyExpression getProperty() {
    return property;
  }

  @Nonnull
  public ClassExpression getFiller() {
    return filler;
  }

  @Override
  public OWLObjectSomeValuesFrom toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectSomeValuesFrom(
        property.toOwlObject(dataFactory),
        filler.toOwlObject(dataFactory));
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
