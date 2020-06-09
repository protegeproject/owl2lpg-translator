package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Required;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "IRI")
public class Iri extends GraphObject implements HasToOwlObject<IRI> {

  @Property
  @Required
  @Index
  private String iri;

  private Iri() {
  }

  @Nullable
  public String getIri() {
    return iri;
  }

  @Override
  public IRI toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return IRI.create(iri);
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 0);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
