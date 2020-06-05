package edu.stanford.owl2lpg.client.read.graph.model;

import com.google.common.base.MoreObjects;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "Class")
public class Class extends ClassExpression<OWLClass> implements Entity {

  private String iri;

  @Relationship(type = "ENTITY_IRI")
  private Iri entityIri;

  private Class() {
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
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("iri", iri)
        .add("entityIri", entityIri)
        .toString();
  }

  @Override
  public OWLClass toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLClass(entityIri.toOwlObject(dataFactory));
  }
}
