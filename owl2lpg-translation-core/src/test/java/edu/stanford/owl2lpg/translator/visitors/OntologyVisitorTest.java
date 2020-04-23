package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.base.Optional;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OntologyVisitorTest {

  private OntologyVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;

  @Mock private OWLOntologyID ontologyId;
  @Mock private Set<OWLAxiom> axioms;
  @Mock private Set<OWLAnnotation> annotations;

  @Mock private NodeId nodeId;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new OntologyVisitor(nodeIdMapper, visitorFactory));
  }

  @Test
  public void shouldVisitOntology() {
    var ontologyIri = mock(IRI.class);
    var ontology = mock(OWLOntology.class);
    when(ontology.getOntologyID()).thenReturn(ontologyId);
    when(ontologyId.getOntologyIRI()).thenReturn(Optional.of(ontologyIri));
    when(ontologyId.getVersionIRI()).thenReturn(Optional.absent());
    when(ontology.getAnnotations()).thenReturn(annotations);
    when(ontology.getAxioms()).thenReturn(axioms);
    when(nodeIdMapper.get(ontologyId)).thenReturn(nodeId);
    when(nodeIdMapper.get(ontology)).thenReturn(nodeId);

    visitor.visit(ontology);
    verify(visitor).visit(ontology);
    verify(visitor).createNode(ontology, NodeLabels.ONTOLOGY_DOCUMENT);
    verify(visitor).createOntologyIdEdge(ontology.getOntologyID());
    verify(visitor).createOntologyIdTranslation(ontology.getOntologyID());
    verify(visitor).createEdges(ontology.getAnnotations(), EdgeLabels.ONTOLOGY_ANNOTATION);
    verify(visitor).createNestedTranslations(ontology.getAnnotations());
    verify(visitor).createEdges(ontology.getAxioms(), EdgeLabels.AXIOM);
    verify(visitor).createNestedTranslations(ontology.getAxioms());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUOEWhenVisitClass() {
    var c = mock(OWLClass.class);
    visitor.visit(c);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUOEWhenVisitObjectProperty() {
    var op = mock(OWLObjectProperty.class);
    visitor.visit(op);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUOEWhenDataPropertyClass() {
    var dp = mock(OWLDataProperty.class);
    visitor.visit(dp);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUOEWhenVisitAnnotationProperty() {
    var ap = mock(OWLClass.class);
    visitor.visit(ap);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUOEWhenVisitDatatype() {
    var dt = mock(OWLDatatype.class);
    visitor.visit(dt);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldThrowUOEWhenVisitNamedIndividual() {
    var ind = mock(OWLClass.class);
    visitor.visit(ind);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenOntologyNull() {
    OWLOntology nullOntology = null;
    visitor.visit(nullOntology);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new OntologyVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new OntologyVisitor(nodeIdMapper, nullVisitorFactory);
  }
}