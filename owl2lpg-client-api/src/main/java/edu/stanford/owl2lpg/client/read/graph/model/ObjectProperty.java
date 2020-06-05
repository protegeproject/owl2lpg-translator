package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ObjectProperty")
public class ObjectProperty
    extends ObjectPropertyExpression<OWLObjectProperty>
    implements Entity {

  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private ObjectProperty() {
  }

  @Override
  @Nonnull
  public String getIri() {
    return iri;
  }

  @Override
  @Nonnull
  public Iri getEntityIri() {
    return entityIri;
  }

  @Override
  public OWLObjectProperty toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLObjectProperty(entityIri.toOwlObject(dataFactory));
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
