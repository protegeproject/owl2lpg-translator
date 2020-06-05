package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;

import javax.annotation.Nonnull;

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

  @Nonnull
  public DataPropertyExpression getProperty() {
    return property;
  }

  @Nonnull
  public Literal getFiller() {
    return filler;
  }

  @Override
  public OWLDataHasValue toOwlObject(OWLDataFactory dataFactory) {
    if (property == null) throw new RuntimeException(this.toString());
    return dataFactory.getOWLDataHasValue(
        property.toOwlObject(dataFactory),
        filler.toOwlObject(dataFactory));
  }
}
