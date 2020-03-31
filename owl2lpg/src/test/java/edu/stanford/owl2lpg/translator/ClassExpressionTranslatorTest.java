package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.datastructure.Graph;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.*;

import static edu.stanford.owl2lpg.datastructure.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.PropertiesFactory.Properties;
import static edu.stanford.owl2lpg.translator.vocab.PropertyNames.IRI;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.NamedIndividual;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectAllValuesFrom;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectComplementOf;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectHasSelf;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectIntersectionOf;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectOneOf;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectSomeValuesFrom;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectUnionOf;

public class ClassExpressionTranslatorTest {

  private ClassExpressionTranslator translator;

  @Before
  public void createTranslator() {
    translator = new ClassExpressionTranslator();
  }

  @Test
  public void shouldTranslateClass() {
    final OWLClass cls = Class(IRI("http://example.org/A"));
    Graph actualGraph = cls.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.CLASS),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/A")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectIntersectionOf() {
    final OWLObjectIntersectionOf ce = ObjectIntersectionOf(
        Class(IRI("http://example.org/Brian")),
        Class(IRI("http://example.org/CanTalk")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_INTERSECTION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Brian")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION),
        Edge(
            Node(NodeLabels.OBJECT_INTERSECTION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/CanTalk")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectUnionOf() {
    final OWLObjectUnionOf ce = ObjectUnionOf(
        Class(IRI("http://example.org/Man")),
        Class(IRI("http://example.org/Woman")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_UNION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Man")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION),
        Edge(
            Node(NodeLabels.OBJECT_UNION_OF),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Woman")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectComplementOf() {
    final OWLObjectComplementOf ce = ObjectComplementOf(Class(IRI("http://example.org/Man")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_COMPLEMENT_OF),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Man")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectOneOf() {
    final OWLObjectOneOf ce = ObjectOneOf(
        NamedIndividual(IRI("http://example.org/Alvin")),
        NamedIndividual(IRI("http://example.org/Simon")),
        NamedIndividual(IRI("http://example.org/Theodore")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_ONE_OF),
            Graph(
                Edge(
                    Node(NodeLabels.NAMED_INDIVIDUAL),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Alvin")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.INDIVIDUAL),
        Edge(
            Node(NodeLabels.OBJECT_ONE_OF),
            Graph(
                Edge(
                    Node(NodeLabels.NAMED_INDIVIDUAL),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Simon")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.INDIVIDUAL),
        Edge(
            Node(NodeLabels.OBJECT_ONE_OF),
            Graph(
                Edge(
                    Node(NodeLabels.NAMED_INDIVIDUAL),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Theodore")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.INDIVIDUAL));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectHasSelf() {
    final OWLObjectHasSelf ce = ObjectHasSelf(ObjectProperty(IRI("http://example.org/like")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_HAS_SELF),
            Graph(
                Edge(
                    Node(NodeLabels.OBJECT_PROPERTY),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/like")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.OBJECT_PROPERTY_EXPRESSION));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectSomeValuesFrom() {
    final OWLObjectSomeValuesFrom ce = ObjectSomeValuesFrom(
        ObjectProperty(IRI("http://example.org/fatherOf")),
        Class(IRI("http://example.org/Man")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_SOME_VALUES_FROM),
            Graph(
                Edge(
                    Node(NodeLabels.OBJECT_PROPERTY),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/fatherOf")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(
            Node(NodeLabels.OBJECT_SOME_VALUES_FROM),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Man")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectAllValuesFrom() {
    final OWLObjectAllValuesFrom ce = ObjectAllValuesFrom(
        ObjectProperty(IRI("http://example.org/hasPet")),
        Class(IRI("http://example.org/Dog")));
    Graph actualGraph = ce.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_ALL_VALUES_FROM),
            Graph(
                Edge(
                    Node(NodeLabels.OBJECT_PROPERTY),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/hasPet")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.OBJECT_PROPERTY_EXPRESSION),
        Edge(
            Node(NodeLabels.OBJECT_ALL_VALUES_FROM),
            Graph(
                Edge(
                    Node(NodeLabels.CLASS),
                    Node(NodeLabels.IRI, Properties(IRI, "http://example.org/Dog")),
                    EdgeLabels.ENTITY_IRI)),
            EdgeLabels.CLASS_EXPRESSION));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateObjectHasValue() {

  }

  @Test
  public void shouldTranslateObjectMinCardinality() {

  }

  @Test
  public void shouldTranslateObjectExactCardinality() {

  }

  @Test
  public void shouldTranslateObjectMaxCardinality() {

  }

  @Test
  public void shouldTranslateDataSomeValuesFrom() {

  }

  @Test
  public void shouldTranslateDataAllValuesFrom() {

  }

  @Test
  public void shouldTranslateDataHasValue() {

  }

  @Test
  public void shouldTranslateDataMinCardinality() {

  }

  @Test
  public void shouldTranslateDataExactCardinality() {

  }

  @Test
  public void shouldTranslateDataMaxCardinality() {

  }
}