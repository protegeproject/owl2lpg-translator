package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HasToOwlObject<O> {

  O toOwlObject(OWLDataFactory dataFactory, Session session);
}
