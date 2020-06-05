package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "IRI")
public class Iri extends GraphObject implements HasToOwlObject<IRI> {

  private String iri;

  private Iri() {
  }

  @Nonnull
  public String getIri() {
    return iri;
  }

  @Override
  public IRI toOwlObject(OWLDataFactory dataFactory) {
    return IRI.create(iri);
  }
}
