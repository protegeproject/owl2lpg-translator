package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

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

  public DataHasValue(@Nonnull DataPropertyExpression property,
                      @Nonnull Literal filler) {
    this.property = checkNotNull(property);
    this.filler = checkNotNull(filler);
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
      var object = session.load(getClass(), getId(), 1);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
