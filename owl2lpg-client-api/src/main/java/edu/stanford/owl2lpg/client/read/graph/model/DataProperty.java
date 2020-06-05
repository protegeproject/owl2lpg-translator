package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "DataProperty")
public class DataProperty extends DataPropertyExpression<OWLDataProperty>
    implements Entity {

  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private DataProperty() {
  }

  @Nonnull
  @Override
  public String getIri() {
    return iri;
  }

  @Nonnull
  @Override
  public Iri getEntityIri() {
    return entityIri;
  }

  @Override
  public OWLDataProperty toOwlObject(OWLDataFactory dataFactory) {
    if (entityIri == null) throw new RuntimeException(this.toString());
    return dataFactory.getOWLDataProperty(entityIri.toOwlObject(dataFactory));
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
