package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "ClassAxiom")
public abstract class ClassAxiom<T extends OWLClassAxiom>
    extends Axiom<T> {

  @Override
  public T toOwlObject(OWLDataFactory dataFactory) {
    throw new RuntimeException("Child class must implement this method");
  }
}
