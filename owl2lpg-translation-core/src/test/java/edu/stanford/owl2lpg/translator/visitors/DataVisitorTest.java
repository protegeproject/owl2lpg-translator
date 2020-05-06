package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLFacet;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DataVisitorTest {

  private DataVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;
  @Mock private EntityVisitor entityVisitor;
  @Mock private DataVisitor dataVisitor;
  @Mock private AnnotationValueVisitor annotationValueVisitor;

  @Mock private IRI anyIri;
  @Mock private OWLDatatype anyDatatype;
  @Mock private OWLLiteral anyLiteral;
  @Mock private Set<OWLLiteral> literals;
  @Mock private OWLDataRange anyDataRange;
  @Mock private Set<OWLDataRange> dataRanges;
  @Mock private Set<OWLFacetRestriction> facetRestrictions;

  @Mock private NodeId nodeId;
  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() {
    when(visitorFactory.getNodeIdMapper()).thenReturn(nodeIdMapper);
    visitor = spy(new DataVisitor(visitorFactory));
    when(visitorFactory.createEntityVisitor()).thenReturn(entityVisitor);
    when(visitorFactory.createAnnotationValueVisitor()).thenReturn(annotationValueVisitor);
    when(visitor.getTranslation(anyDataRange)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyLiteral)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyDatatype)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitDatatype() {
    var dt = mock(OWLDatatype.class);
    visitor.visit(dt);
    verify(visitor).visit(dt);
    verify(visitorFactory).createEntityVisitor();
    verify(entityVisitor).visit(dt);
  }

  @Test
  public void shouldVisitTypedLiteral() {
    var lt = mock(OWLLiteral.class);
    when(lt.getLiteral()).thenReturn("value");
    when(lt.getDatatype()).thenReturn(anyDatatype);
    when(anyDatatype.toStringID()).thenReturn("datatypeId");
    when(anyDatatype.getIRI()).thenReturn(anyIri);
    when(lt.isRDFPlainLiteral()).thenReturn(false);
    when(lt.getLang()).thenReturn("");
    var ltWrap = LiteralWrapper.create(lt);
    when(nodeIdMapper.get(ltWrap)).thenReturn(nodeId);

    visitor.visit(lt);
    verify(visitor).visit(lt);
    verify(visitor).createLiteralNode(lt, NodeLabels.LITERAL);
    verify(visitor).createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createNestedTranslation(lt.getDatatype());
    verify(visitor, times(0))
        .createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
    verify(visitor, times(0))
        .createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
  }

  @Test
  public void shoudVisitPlainLiteral() {
    var lt = mock(OWLLiteral.class);
    when(lt.getLiteral()).thenReturn("value");
    when(lt.getDatatype()).thenReturn(anyDatatype);
    when(anyDatatype.getIRI()).thenReturn(anyIri);
    when(anyDatatype.toStringID()).thenReturn("datatypeId");
    when(lt.isRDFPlainLiteral()).thenReturn(true);
    when(lt.hasLang()).thenReturn(false);
    when(lt.getLang()).thenReturn("");
    var ltWrap = LiteralWrapper.create(lt);
    when(nodeIdMapper.get(ltWrap)).thenReturn(nodeId);

    visitor.visit(lt);
    verify(visitor).visit(lt);
    verify(visitor).createLiteralNode(lt, NodeLabels.LITERAL);
    verify(visitor).createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createNestedTranslation(lt.getDatatype());
    verify(visitor, times(0))
        .createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
    verify(visitor, times(0))
        .createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
  }

  @Test
  public void shoudVisitPlainLiteralWithoutLanguageTag() {
    var lt = mock(OWLLiteral.class);
    when(lt.getLiteral()).thenReturn("value");
    when(lt.getDatatype()).thenReturn(anyDatatype);
    when(anyDatatype.getIRI()).thenReturn(anyIri);
    when(anyDatatype.toStringID()).thenReturn("datatypeId");
    when(lt.isRDFPlainLiteral()).thenReturn(true);
    when(lt.hasLang()).thenReturn(true);
    when(lt.getLang()).thenReturn("lang");
    var ltWrap = LiteralWrapper.create(lt);
    when(nodeIdMapper.get(ltWrap)).thenReturn(nodeId);
    when(nodeIdMapper.get("lang")).thenReturn(nodeId);

    visitor.visit(lt);
    verify(visitor).visit(lt);
    verify(visitor).createLiteralNode(lt, NodeLabels.LITERAL);
    verify(visitor).createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createNestedTranslation(lt.getDatatype());
    verify(visitor).createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
    verify(visitor, times(2)).createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
  }

  @Test
  public void shouldVisitDataComplementOf() {
    var dr = mock(OWLDataComplementOf.class);
    when(dr.getDataRange()).thenReturn(anyDataRange);
    when(nodeIdMapper.get(dr)).thenReturn(nodeId);

    visitor.visit(dr);
    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_COMPLEMENT_OF);
    verify(visitor).createEdge(dr.getDataRange(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(dr.getDataRange());
  }

  @Test
  public void shouldVisitDataOneOf() {
    var dr = mock(OWLDataOneOf.class);
    when(dr.getValues()).thenReturn(literals);
    when(nodeIdMapper.get(dr)).thenReturn(nodeId);

    visitor.visit(dr);
    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_ONE_OF);
    verify(visitor).createEdges(dr.getValues(), EdgeLabels.LITERAL);
    verify(visitor).createNestedTranslations(dr.getValues());
  }

  @Test
  public void shouldVisitDataIntersectionOf() {
    var dr = mock(OWLDataIntersectionOf.class);
    when(dr.getOperands()).thenReturn(dataRanges);
    when(nodeIdMapper.get(dr)).thenReturn(nodeId);

    visitor.visit(dr);
    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_INTERSECTION_OF);
    verify(visitor).createEdges(dr.getOperands(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslations(dr.getOperands());
  }

  @Test
  public void shouldVisitDataUnionOf() {
    var dr = mock(OWLDataUnionOf.class);
    when(dr.getOperands()).thenReturn(dataRanges);
    when(nodeIdMapper.get(dr)).thenReturn(nodeId);

    visitor.visit(dr);
    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_UNION_OF);
    verify(visitor).createEdges(dr.getOperands(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslations(dr.getOperands());
  }

  @Test
  public void shouldVisitDatatypeRestriction() {
    var dr = mock(OWLDatatypeRestriction.class);
    when(dr.getDatatype()).thenReturn(anyDatatype);
    when(dr.getFacetRestrictions()).thenReturn(facetRestrictions);
    when(nodeIdMapper.get(dr)).thenReturn(nodeId);

    visitor.visit(dr);
    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATATYPE_RESTRICTION);
    verify(visitor).createEdge(dr.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createNestedTranslation(dr.getDatatype());
    verify(visitor).createEdges(dr.getFacetRestrictions(), EdgeLabels.RESTRICTION);
    verify(visitor).createNestedTranslations(dr.getFacetRestrictions());
  }

  @Ignore
  @Test
  public void shouldVisitFacetRestriction() {
    var restriction = mock(OWLFacetRestriction.class);
    var facet = OWLFacet.LENGTH;
    when(restriction.getFacet()).thenReturn(facet);
    when(restriction.getFacetValue()).thenReturn(anyLiteral);
    when(nodeIdMapper.get(restriction)).thenReturn(nodeId);

    visitor.visit(restriction);
    verify(visitor).visit(restriction);
    verify(visitor).createNode(restriction, NodeLabels.FACET_RESTRICTION);
    verify(visitor).createEdge(restriction.getFacet().getIRI(), EdgeLabels.CONSTRAINING_FACET);
    verify(visitor).createNestedTranslation(restriction.getFacet().getIRI());
    verify(visitor).createEdge(restriction.getFacetValue(), EdgeLabels.RESTRICTION_VALUE);
    verify(visitor).createNestedTranslation(restriction.getFacetValue());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new DataVisitor(nullVisitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenLiteralNull() {
    OWLLiteral nullLiteral = null;
    visitor.visit(nullLiteral);
  }
}