package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectMinCardinality")
public class ObjectMinCardinality extends ClassExpression<OWLObjectMinCardinality> {

  @Property
  private Integer cardinality;

  @Relationship(type = "OBJECT_PROPERTY_EXPRESSION")
  private ObjectPropertyExpression property;

  @Relationship(type = "CLASS_EXPRESSION")
  private ClassExpression filler;

  private ObjectMinCardinality() {
  }

  @Nullable
  public Integer getCardinality() {
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
  public OWLObjectMinCardinality toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (property == null || filler == null || cardinality == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLObjectMinCardinality(
          getCardinality(),
          property.toOwlObject(dataFactory, session),
          filler.toOwlObject(dataFactory, session));
    }
  }

  private ObjectMinCardinality reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
  }
}
