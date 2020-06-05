package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "DataHasValue")
public class DataHasValue extends ClassExpression<OWLDataHasValue> {

  @Relationship(type = "DATA_PROPERTY_EXPRESSION")
  private DataPropertyExpression property;

  @Relationship(type = "LITERAL")
  private Literal filler;

  private DataHasValue() {
  }

  @Nullable
  public DataPropertyExpression getProperty() {
    return property;
  }

  @Nullable
  public Literal getFiller() {
    return filler;
  }

  @Override
  public OWLDataHasValue toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLDataHasValue(
          property.toOwlObject(dataFactory, session),
          filler.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 3);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
