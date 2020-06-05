package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "DataPropertyExpression")
public abstract class DataPropertyExpression<T extends OWLDataPropertyExpression>
    extends GraphObject
    implements HasToOwlObject<T> {

  @Override
  public T toOwlObject(OWLDataFactory dataFactory) {
    throw new RuntimeException("Child class must implement this method");
  }
}
