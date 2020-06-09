package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectProperty")
public class ObjectProperty
    extends ObjectPropertyExpression<OWLObjectProperty>
    implements Entity {

  @Property
  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private ObjectProperty() {
  }

  @Nullable
  @Override
  public String getIri() {
    return iri;
  }

  @Nullable
  @Override
  public Iri getEntityIri() {
    return entityIri;
  }

  @Override
  public OWLObjectProperty toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLObjectProperty(entityIri.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 2);
      return object.toOwlObject(dataFactory, session);
    }
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("iri", iri)
        .add("entityIri", entityIri)
        .toString();
  }
}
