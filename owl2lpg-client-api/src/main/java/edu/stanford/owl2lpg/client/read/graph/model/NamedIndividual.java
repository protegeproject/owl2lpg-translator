package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "NamedIndividual")
public class NamedIndividual extends Individual<OWLNamedIndividual>
    implements Entity {

  @Property
  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private NamedIndividual() {
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
  public OWLNamedIndividual toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLNamedIndividual(entityIri.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 1);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
