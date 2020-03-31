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
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.AnnotationProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Class;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.DataProperty;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.Datatype;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.IRI;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.NamedIndividual;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.ObjectProperty;

public class EntityTranslatorTest {

  private EntityTranslator translator;

  @Before
  public void createVisitor() {
    translator = new EntityTranslator();
  }

  @Test
  public void shouldTranslateEntityClass() {
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
  public void shouldTranslateEntityDatatype() {
    final OWLDatatype dt = Datatype(IRI("http://example.org/atype"));
    Graph actualGraph = dt.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATATYPE),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/atype")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityObjectProperty() {
    final OWLObjectProperty op = ObjectProperty(IRI("http://example.org/p"));
    Graph actualGraph = op.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.OBJECT_PROPERTY),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/p")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityDataProperty() {
    final OWLDataProperty dp = DataProperty(IRI("http://example.org/q"));
    Graph actualGraph = dp.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.DATA_PROPERTY),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/q")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityAnnotationProperty() {
    final OWLAnnotationProperty ap = AnnotationProperty(IRI("http://example.org/m"));
    Graph actualGraph = ap.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.ANNOTATION_PROPERTY),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/m")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }

  @Test
  public void shouldTranslateEntityNamedIndividual() {
    final OWLNamedIndividual a = NamedIndividual(IRI("http://example.org/ind"));
    Graph actualGraph = a.accept(translator);
    Graph expectedGraph = Graph(
        Edge(
            Node(NodeLabels.NAMED_INDIVIDUAL),
            Node(NodeLabels.IRI, Properties(IRI, "http://example.org/ind")),
            EdgeLabels.ENTITY_IRI));
    assertThat(actualGraph, equalTo(expectedGraph));
  }
}