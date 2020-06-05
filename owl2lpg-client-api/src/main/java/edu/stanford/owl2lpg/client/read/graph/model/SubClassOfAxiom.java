package edu.stanford.owl2lpg.client.read.graph.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@NodeEntity(label = "SubClassOf")
public class SubClassOfAxiom extends ClassAxiom<OWLSubClassOfAxiom> {

  @Nonnull
  private String iri;

  @Nonnull
  @Relationship(type = "SUB_CLASS_EXPRESSION")
  private ClassExpression subClass;

  @Nonnull
  @Relationship(type = "SUPER_CLASS_EXPRESSION")
  private ClassExpression superClass;

  private SubClassOfAxiom() {
  }

  @Nonnull
  public String getIri() {
    return iri;
  }

  @Nonnull
  public ClassExpression getSubClass() {
    return subClass;
  }

  @Nonnull
  public ClassExpression getSuperClass() {
    return superClass;
  }

  @Override
  public OWLSubClassOfAxiom toOwlObject(OWLDataFactory dataFactory) {
    return dataFactory.getOWLSubClassOfAxiom(
        subClass.toOwlObject(dataFactory),
        superClass.toOwlObject(dataFactory));
  }
}
