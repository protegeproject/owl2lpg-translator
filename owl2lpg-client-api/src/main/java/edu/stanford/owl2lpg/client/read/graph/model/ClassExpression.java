package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ClassExpression")
public abstract class ClassExpression<T extends OWLClassExpression>
    extends GraphObject
    implements HasToOwlObject<T> {

  @Override
  public T toOwlObject(OWLDataFactory dataFactory, Session session) {
    throw new RuntimeException("Child class must implement this method");
  }
}
