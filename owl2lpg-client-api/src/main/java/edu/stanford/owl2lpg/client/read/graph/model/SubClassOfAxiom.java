package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nullable;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "SubClassOf")
public class SubClassOfAxiom extends ClassAxiom<OWLSubClassOfAxiom> {

  @Property
  private String iri;

  @Relationship(type = "SUB_CLASS_EXPRESSION")
  private ClassExpression subClass;

  @Relationship(type = "SUPER_CLASS_EXPRESSION")
  private ClassExpression superClass;

  private SubClassOfAxiom() {
  }

  @Nullable
  public String getIri() {
    return iri;
  }

  @Nullable
  public ClassExpression getSubClass() {
    return subClass;
  }

  @Nullable
  public ClassExpression getSuperClass() {
    return superClass;
  }

  @Override
  public OWLSubClassOfAxiom toOwlObject(OWLDataFactory dataFactory, Session session) {
    try {
      return dataFactory.getOWLSubClassOfAxiom(
          subClass.toOwlObject(dataFactory, session),
          superClass.toOwlObject(dataFactory, session));
    } catch (NullPointerException e) {
      var object = session.load(getClass(), getId(), 2);
      return object.toOwlObject(dataFactory, session);
    }
  }
}
