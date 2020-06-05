package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "NamedIndividual")
public class NamedIndividual extends Individual<OWLNamedIndividual>
    implements Entity {

  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private NamedIndividual() {
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
  public OWLNamedIndividual toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLNamedIndividual(entityIri.toOwlObject(dataFactory));
  }
}
