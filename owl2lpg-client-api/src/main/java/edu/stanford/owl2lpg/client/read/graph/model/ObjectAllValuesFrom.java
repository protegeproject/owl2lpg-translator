package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectAllValuesFrom")
public class ObjectAllValuesFrom extends ClassExpression<OWLObjectAllValuesFrom> {

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectAllValuesFrom() {
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
  public OWLObjectAllValuesFrom toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectAllValuesFrom(
        property.toOwlObject(dataFactory),
        filler.toOwlObject(dataFactory));
  }
}
