package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "Class")
public class NamedClass extends ClassExpression<OWLClass>
    implements Entity<OWLClass> {

  @Property
  @Required
  @Index
  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private NamedClass() {
  }

  public NamedClass(@Nonnull String iri, @Nonnull Iri entityIri) {
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
  public OWLClass toOwlObject(OWLDataFactory dataFactory, Session session) {
    if (entityIri == null) {
      var nodeEntity = reloadThisNodeEntity(session);
      return nodeEntity.toOwlObject(dataFactory, session);
    } else {
      return dataFactory.getOWLClass(entityIri.toOwlObject(dataFactory, session));
    }
  }

  private NamedClass reloadThisNodeEntity(Session session) {
    return session.load(getClass(), getId(), 1);
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
