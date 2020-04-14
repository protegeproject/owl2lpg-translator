package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLFacet;

import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataVisitorTest {

  private DataVisitor visitor;

  @Mock
  private EntityVisitor entityVisitor;

  @Mock
  private OWLDatatype datatype;

  @Mock
  private OWLLiteral literal;

  @Mock
  private Set<OWLLiteral> literals;

  @Mock
  private OWLDataRange anyDataRange;

  @Mock
  private Set<OWLDataRange> anyDataRanges;

  @Mock
  private Set<OWLFacetRestriction> facetRestrictions;

  @Mock
  private Translation nestedTranslation;

  @Mock
  private Node nestedTranslationMainNode;

  @Before
  public void setUp() {
    visitor = spy(new DataVisitor(entityVisitor));
    when(anyDataRange.accept(visitor)).thenReturn(nestedTranslation);
    when(literal.accept(visitor)).thenReturn(nestedTranslation);
    when(datatype.accept(visitor)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitDatatype() {
    var dt = mock(OWLDatatype.class);
    visitor.visit(dt);

    verify(visitor).visit(dt);
    verify(entityVisitor).visit(dt);
  }

  @Test
  public void shouldVisitTypedLiteral() {
    var lt = mock(OWLLiteral.class);
    when(lt.getLiteral()).thenReturn("value");
    when(lt.getDatatype()).thenReturn(datatype);
    when(lt.isRDFPlainLiteral()).thenReturn(false);
    visitor.visit(lt);

    verify(visitor).visit(lt);
    verify(visitor).createLiteralNode(lt, NodeLabels.LITERAL);
    verify(visitor).createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createTranslation(lt.getDatatype());
    verify(visitor, times(0))
        .createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
    verify(visitor, times(0))
        .createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
  }

  @Test
  public void shoudVisitPlainLiteral() {
    var lt = mock(OWLLiteral.class);
    when(lt.getLiteral()).thenReturn("value");
    when(lt.getDatatype()).thenReturn(datatype);
    when(lt.isRDFPlainLiteral()).thenReturn(true);
    when(lt.hasLang()).thenReturn(false);
    visitor.visit(lt);

    verify(visitor).visit(lt);
    verify(visitor).createLiteralNode(lt, NodeLabels.LITERAL);
    verify(visitor).createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createTranslation(lt.getDatatype());
    verify(visitor, times(0))
        .createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
    verify(visitor, times(0))
        .createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
  }

  @Test
  public void shoudVisitPlainLiteralWithoutLanguageTag() {
    var lt = mock(OWLLiteral.class);
    when(lt.getLiteral()).thenReturn("value");
    when(lt.getDatatype()).thenReturn(datatype);
    when(lt.isRDFPlainLiteral()).thenReturn(true);
    when(lt.hasLang()).thenReturn(true);
    when(lt.getLang()).thenReturn("lang");
    visitor.visit(lt);

    verify(visitor).visit(lt);
    verify(visitor).createLiteralNode(lt, NodeLabels.LITERAL);
    verify(visitor).createEdge(lt.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createTranslation(lt.getDatatype());
    verify(visitor).createLanguageTagEdge(lt.getLang(), EdgeLabels.LANGUAGE_TAG);
    verify(visitor, times(2)).createLanguageTagNode(lt.getLang(), NodeLabels.LANGUAGE_TAG);
  }

  @Test
  public void shouldVisitDataComplementOf() {
    var dr = mock(OWLDataComplementOf.class);
    when(dr.getDataRange()).thenReturn(anyDataRange);
    visitor.visit(dr);

    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_COMPLEMENT_OF);
    verify(visitor).createEdge(dr.getDataRange(), EdgeLabels.DATA_RANGE);
    verify(visitor).createTranslation(dr.getDataRange());
  }

  @Test
  public void shouldVisitDataOneOf() {
    var dr = mock(OWLDataOneOf.class);
    when(dr.getValues()).thenReturn(literals);
    visitor.visit(dr);

    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_ONE_OF);
    verify(visitor).createEdges(dr.getValues(), EdgeLabels.LITERAL);
    verify(visitor).createTranslations(dr.getValues());
  }

  @Test
  public void shouldVisitDataIntersectionOf() {
    var dr = mock(OWLDataIntersectionOf.class);
    when(dr.getOperands()).thenReturn(anyDataRanges);
    visitor.visit(dr);

    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_INTERSECTION_OF);
    verify(visitor).createEdges(dr.getOperands(), EdgeLabels.DATA_RANGE);
    verify(visitor).createTranslations(dr.getOperands());
  }

  @Test
  public void shouldVisitDataUnionOf() {
    var dr = mock(OWLDataUnionOf.class);
    when(dr.getOperands()).thenReturn(anyDataRanges);
    visitor.visit(dr);

    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATA_UNION_OF);
    verify(visitor).createEdges(dr.getOperands(), EdgeLabels.DATA_RANGE);
    verify(visitor).createTranslations(dr.getOperands());
  }

  @Test
  public void shouldVisitDatatypeRestriction() {
    var dr = mock(OWLDatatypeRestriction.class);
    when(dr.getDatatype()).thenReturn(datatype);
    when(dr.getFacetRestrictions()).thenReturn(facetRestrictions);
    visitor.visit(dr);

    verify(visitor).visit(dr);
    verify(visitor).createNode(dr, NodeLabels.DATATYPE_RESTRICTION);
    verify(visitor).createEdge(dr.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createTranslation(dr.getDatatype());
    verify(visitor).createEdges(dr.getFacetRestrictions(), EdgeLabels.RESTRICTION);
    verify(visitor).createTranslations(dr.getFacetRestrictions());
  }

  @Test
  public void shouldVisitFacetRestriction() {
    var restriction = mock(OWLFacetRestriction.class);
    var facet = OWLFacet.LENGTH;
    when(restriction.getFacet()).thenReturn(facet);
    when(restriction.getFacetValue()).thenReturn(literal);
    visitor.visit(restriction);

    verify(visitor).visit(restriction);
    verify(visitor).createNode(restriction, NodeLabels.FACET_RESTRICTION);
    verify(visitor).createEdge(restriction.getFacet().getIRI(), EdgeLabels.CONSTRAINING_FACET);
    verify(visitor).createTranslation(restriction.getFacet().getIRI());
    verify(visitor).createEdge(restriction.getFacetValue(), EdgeLabels.RESTRICTION_VALUE);
    verify(visitor).createTranslation(restriction.getFacetValue());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEntityVisitorNull() {
    EntityVisitor nullEntityVisitor = null;
    new DataVisitor(nullEntityVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenLiteralNull() {
    OWLLiteral nullLiteral = null;
    visitor.visit(nullLiteral);
  }
}