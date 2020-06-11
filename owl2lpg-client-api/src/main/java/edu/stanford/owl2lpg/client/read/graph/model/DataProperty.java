package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "DataProperty")
public class DataProperty extends DataPropertyExpression<OWLDataProperty>
    implements Entity<OWLDataProperty> {

  @Property
  @Required
  @Index
  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private DataProperty() {
  }

  public DataProperty(@Nonnull String iri,
                      @Nonnull Iri entityIri) {
    this.iri = checkNotNull(iri);
    this.entityIri = checkNotNull(entityIri);
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
  public OWLDataProperty toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLDataProperty(entityIri.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 1);
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
