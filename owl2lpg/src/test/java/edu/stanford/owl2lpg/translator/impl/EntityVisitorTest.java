package edu.stanford.owl2lpg.translator.impl;

import edu.stanford.owl2lpg.datastructure.Edge;
import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.datastructure.Node;
import edu.stanford.owl2lpg.translator.EdgeLabels;
import edu.stanford.owl2lpg.translator.NodeLabels;
import edu.stanford.owl2lpg.translator.PropertyNames;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

public class EntityVisitorTest {

  private EntityVisitor visitor;

  @Before
  public void createVisitor() {
    visitor = new EntityVisitor();
  }

  @Test
  public void shouldTranslateEntityClass() {
    final OWLClass cls = Class(IRI("http://example.org/A"));
    Graph actualGraph = cls.accept(visitor);
    Graph expectedGraph = Graph.create(
        Edge.create(
            Node.create(NodeLabels.CLASS),
            Node.create(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://example.org/A").build()),
            EdgeLabels.ENTITY_IRI
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityDatatype() {
    final OWLDatatype dt = Datatype(IRI("http://example.org/atype"));
    Graph actualGraph = dt.accept(visitor);
    Graph expectedGraph = Graph.create(
        Edge.create(
            Node.create(NodeLabels.DATATYPE),
            Node.create(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://example.org/atype").build()),
            EdgeLabels.ENTITY_IRI
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityObjectProperty() {
    final OWLObjectProperty op = ObjectProperty(IRI("http://example.org/p"));
    Graph actualGraph = op.accept(visitor);
    Graph expectedGraph = Graph.create(
        Edge.create(
            Node.create(NodeLabels.OBJECT_PROPERTY),
            Node.create(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://example.org/p").build()),
            EdgeLabels.ENTITY_IRI
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityDataProperty() {
    final OWLDataProperty dp = DataProperty(IRI("http://example.org/q"));
    Graph actualGraph = dp.accept(visitor);
    Graph expectedGraph = Graph.create(
        Edge.create(
            Node.create(NodeLabels.DATA_PROPERTY),
            Node.create(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://example.org/q").build()),
            EdgeLabels.ENTITY_IRI
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityAnnotationProperty() {
    final OWLAnnotationProperty ap = AnnotationProperty(IRI("http://example.org/m"));
    Graph actualGraph = ap.accept(visitor);
    Graph expectedGraph = Graph.create(
        Edge.create(
            Node.create(NodeLabels.ANNOTATION_PROPERTY),
            Node.create(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://example.org/m").build()),
            EdgeLabels.ENTITY_IRI
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityNamedIndividual() {
    final OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/ind"));
    Graph actualGraph = a.accept(visitor);
    Graph expectedGraph = Graph.create(
        Edge.create(
            Node.create(NodeLabels.NAMED_INDIVIDUAL),
            Node.create(NodeLabels.IRI, PropertiesBuilder.create()
                .set(PropertyNames.IRI, "http://example.org/ind").build()),
            EdgeLabels.ENTITY_IRI
        )
    );
    assertThat(actualGraph, equalTo(expectedGraph));
  }
}