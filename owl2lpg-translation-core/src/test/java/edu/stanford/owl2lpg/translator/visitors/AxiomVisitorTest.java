package edu.stanford.owl2lpg.translator.visitors;

import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AxiomVisitorTest {

  private AxiomVisitor visitor;

  // @formatter:off
  @Mock private NodeIdMapper nodeIdMapper;
  @Mock private VisitorFactory visitorFactory;

  @Mock private OWLClass anyClass;
  @Mock private OWLEntity anyEntity;
  @Mock private OWLClassExpression anyClassExpression;
  @Mock private OWLClassExpression anySubClassExpression;
  @Mock private OWLClassExpression anySuperClassExpression;
  @Mock private Set<OWLClassExpression> classExpressions;
  @Mock private OWLObjectPropertyExpression anyObjectPropertyExpression;
  @Mock private OWLObjectPropertyExpression anySubObjectPropertyExpression;
  @Mock private OWLObjectPropertyExpression anySuperObjectPropertyExpression;
  @Mock private Set<OWLObjectPropertyExpression> objectPropertyExpressions;
  @Mock private List<OWLObjectPropertyExpression> listOfObjectPropertyExpressions;
  @Mock private OWLDataPropertyExpression anyDataPropertyExpression;
  @Mock private OWLDataPropertyExpression anySubDataPropertyExpression;
  @Mock private OWLDataPropertyExpression anySuperDataPropertyExpression;
  @Mock private Set<OWLDataPropertyExpression> dataPropertyExpressions;
  @Mock private OWLAnnotationProperty anyAnnotationProperty;
  @Mock private OWLAnnotationProperty anySubAnnotationProperty;
  @Mock private OWLAnnotationProperty anySuperAnnotationProperty;
  @Mock private OWLIndividual anyIndividual;
  @Mock private OWLIndividual anySourceIndividual;
  @Mock private OWLIndividual anyTargetIndividual;
  @Mock private Set<OWLIndividual> individuals;
  @Mock private OWLDatatype anyDatatype;
  @Mock private OWLDataRange anyDataRange;
  @Mock private OWLLiteral anyLiteral;
  @Mock private IRI anyIri;
  @Mock private Set<OWLAnnotation> annotations;
  @Mock private OWLAnnotationSubject anyAnnotationSubject;
  @Mock private OWLAnnotationValue anyAnnotationValue;

  @Mock private NodeId nodeId;
  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() {
    visitor = spy(new AxiomVisitor(nodeIdMapper, visitorFactory));
    when(visitor.getTranslation(anyClass)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyDatatype)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyAnnotationProperty)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySubAnnotationProperty)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySuperAnnotationProperty)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyEntity)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyClassExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySubClassExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySuperClassExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyObjectPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySubObjectPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySuperObjectPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyDataPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySubDataPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySuperDataPropertyExpression)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyIndividual)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anySourceIndividual)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyTargetIndividual)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyDataRange)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyLiteral)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyIri)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyAnnotationSubject)).thenReturn(nestedTranslation);
    when(visitor.getTranslation(anyAnnotationValue)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitDeclarationAxiom() {
    var axiom = mock(OWLDeclarationAxiom.class);
    when(axiom.getEntity()).thenReturn(anyEntity);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DECLARATION);
    verify(visitor).createEdge(axiom.getEntity(), EdgeLabels.ENTITY);
    verify(visitor).createNestedTranslation(axiom.getEntity());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDatatypeDefinitionAxiom() {
    var axiom = mock(OWLDatatypeDefinitionAxiom.class);
    when(axiom.getDatatype()).thenReturn(anyDatatype);
    when(axiom.getDataRange()).thenReturn(anyDataRange);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATATYPE_DEFINITION);
    verify(visitor).createEdge(axiom.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createNestedTranslation(axiom.getDatatype());
    verify(visitor).createEdge(axiom.getDataRange(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(axiom.getDataRange());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSubClassOfAxiom() {
    var axiom = mock(OWLSubClassOfAxiom.class);
    when(axiom.getSubClass()).thenReturn(anySubClassExpression);
    when(axiom.getSuperClass()).thenReturn(anySuperClassExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_CLASS_OF);
    verify(visitor).createEdge(axiom.getSubClass(), EdgeLabels.SUB_CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSubClass());
    verify(visitor).createEdge(axiom.getSuperClass(), EdgeLabels.SUPER_CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSuperClass());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSubObjectPropertyOfAxiom() {
    var axiom = mock(OWLSubObjectPropertyOfAxiom.class);
    when(axiom.getSubProperty()).thenReturn(anySubObjectPropertyExpression);
    when(axiom.getSuperProperty()).thenReturn(anySuperObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getSubProperty(), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSubProperty());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSuperProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSubDataPropertyOfAxiom() {
    var axiom = mock(OWLSubDataPropertyOfAxiom.class);
    when(axiom.getSubProperty()).thenReturn(anySubDataPropertyExpression);
    when(axiom.getSuperProperty()).thenReturn(anySuperDataPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getSubProperty(), EdgeLabels.SUB_DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSubProperty());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSuperProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSubPropertyChainOfAxiom() {
    var axiom = mock(OWLSubPropertyChainOfAxiom.class);
    when(axiom.getPropertyChain()).thenReturn(listOfObjectPropertyExpressions);
    when(axiom.getPropertyChain().get(0)).thenReturn(anyObjectPropertyExpression);
    when(axiom.getPropertyChain().size()).thenReturn(1);
    when(axiom.getSuperProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getPropertyChain().get(0), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createChainTranslation(axiom.getPropertyChain());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor, times(2)).createNestedTranslation(axiom.getSuperProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSubAnnotationPropertyOfAxiom() {
    var axiom = mock(OWLSubAnnotationPropertyOfAxiom.class);
    when(axiom.getSubProperty()).thenReturn(anySubAnnotationProperty);
    when(axiom.getSuperProperty()).thenReturn(anySuperAnnotationProperty);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getSubProperty(), EdgeLabels.SUB_ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getSubProperty());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getSuperProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitObjectPropertyDomainAxiom() {
    var axiom = mock(OWLObjectPropertyDomainAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getDomain()).thenReturn(anyClassExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.OBJECT_PROPERTY_DOMAIN);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    verify(visitor).createNestedTranslation(axiom.getDomain());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitObjectPropertyRangeAxiom() {
    var axiom = mock(OWLObjectPropertyRangeAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getRange()).thenReturn(anyClassExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.OBJECT_PROPERTY_RANGE);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getRange(), EdgeLabels.RANGE);
    verify(visitor).createNestedTranslation(axiom.getRange());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDataPropertyDomainAxiom() {
    var axiom = mock(OWLDataPropertyDomainAxiom.class);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getDomain()).thenReturn(anyClassExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATA_PROPERTY_DOMAIN);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    verify(visitor).createNestedTranslation(axiom.getDomain());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDataPropertyRangeAxiom() {
    var axiom = mock(OWLDataPropertyRangeAxiom.class);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getRange()).thenReturn(anyDataRange);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATA_PROPERTY_RANGE);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getRange(), EdgeLabels.RANGE);
    verify(visitor).createNestedTranslation(axiom.getRange());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitAnnotationPropertyDomainAxiom() {
    var axiom = mock(OWLAnnotationPropertyDomainAxiom.class);
    when(axiom.getProperty()).thenReturn(anyAnnotationProperty);
    when(axiom.getDomain()).thenReturn(anyIri);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_DOMAIN);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    verify(visitor).createNestedTranslation(axiom.getDomain());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitAnnotationPropertyRangeAxiom() {
    var axiom = mock(OWLAnnotationPropertyRangeAxiom.class);
    when(axiom.getProperty()).thenReturn(anyAnnotationProperty);
    when(axiom.getRange()).thenReturn(anyIri);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_RANGE);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getRange(), EdgeLabels.RANGE);
    verify(visitor).createNestedTranslation(axiom.getRange());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitEquivalentClassesAxiom() {
    var axiom = mock(OWLEquivalentClassesAxiom.class);
    when(axiom.getClassExpressions()).thenReturn(classExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.EQUIVALENT_CLASSES);
    verify(visitor).createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getClassExpressions());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitEquivalentObjectPropertiesAxiom() {
    var axiom = mock(OWLEquivalentObjectPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(objectPropertyExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.EQUIVALENT_OBJECT_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitEquivalentDataPropertiesAxiom() {
    var axiom = mock(OWLEquivalentDataPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(dataPropertyExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.EQUIVALENT_DATA_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDisjointClassesAxiom() {
    var axiom = mock(OWLDisjointClassesAxiom.class);
    when(axiom.getClassExpressions()).thenReturn(classExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_CLASSES);
    verify(visitor).createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getClassExpressions());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDisjointUnionAxiom() {
    var axiom = mock(OWLDisjointUnionAxiom.class);
    when(axiom.getOWLClass()).thenReturn(anyClass);
    when(axiom.getClassExpressions()).thenReturn(classExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_UNION);
    verify(visitor).createEdge(axiom.getOWLClass(), EdgeLabels.CLASS);
    verify(visitor).createNestedTranslation(axiom.getOWLClass());
    verify(visitor).createEdges(axiom.getClassExpressions(), EdgeLabels.DISJOINT_CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getClassExpressions());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDisjointObjectPropertiesAxiom() {
    var axiom = mock(OWLDisjointObjectPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(objectPropertyExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_OBJECT_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDisjointDataPropertiesAxiom() {
    var axiom = mock(OWLDisjointDataPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(dataPropertyExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_DATA_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSameIndividualAxiom() {
    var axiom = mock(OWLSameIndividualAxiom.class);
    when(axiom.getIndividuals()).thenReturn(individuals);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    verify(visitor).createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslations(axiom.getIndividuals());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDifferentIndividualsAxiom() {
    var axiom = mock(OWLDifferentIndividualsAxiom.class);
    when(axiom.getIndividuals()).thenReturn(individuals);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DIFFERENT_INDIVIDUALS);
    verify(visitor).createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslations(axiom.getIndividuals());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitInverseObjectPropertiesAxiom() {
    var axiom = mock(OWLInverseObjectPropertiesAxiom.class);
    when(axiom.getFirstProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getSecondProperty()).thenReturn(anySuperObjectPropertyExpression); // any ope
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.INVERSE_OBJECT_PROPERTIES);
    verify(visitor).createEdge(axiom.getFirstProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getFirstProperty());
    verify(visitor).createEdge(axiom.getSecondProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSecondProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitHasKeyAxiom() {
    var axiom = mock(OWLHasKeyAxiom.class);
    when(axiom.getClassExpression()).thenReturn(anyClassExpression);
    when(axiom.getObjectPropertyExpressions()).thenReturn(objectPropertyExpressions);
    when(axiom.getDataPropertyExpressions()).thenReturn(dataPropertyExpressions);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.HAS_KEY);
    verify(visitor).createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getClassExpression());
    verify(visitor).createEdges(axiom.getObjectPropertyExpressions(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getObjectPropertyExpressions());
    verify(visitor).createEdges(axiom.getDataPropertyExpressions(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getDataPropertyExpressions());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitClassAssertionAxiom() {
    var axiom = mock(OWLClassAssertionAxiom.class);
    when(axiom.getClassExpression()).thenReturn(anyClassExpression);
    when(axiom.getIndividual()).thenReturn(anyIndividual);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.CLASS_ASSERTION);
    verify(visitor).createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getClassExpression());
    verify(visitor).createEdge(axiom.getIndividual(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getIndividual());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitObjectPropertyAssertionAxiom() {
    var axiom = mock(OWLObjectPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anySourceIndividual);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getObject()).thenReturn(anyTargetIndividual);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.OBJECT_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getObject());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitDataPropertyAssertionAxiom() {
    var axiom = mock(OWLDataPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anyIndividual);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getObject()).thenReturn(anyLiteral);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATA_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_VALUE);
    verify(visitor).createNestedTranslation(axiom.getObject());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitAnnotationAssertionAxiom() {
    var axiom = mock(OWLAnnotationAssertionAxiom.class);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);
    when(axiom.getSubject()).thenReturn(anyAnnotationSubject);
    when(axiom.getProperty()).thenReturn(anyAnnotationProperty);
    when(axiom.getValue()).thenReturn(anyAnnotationValue);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ANNOTATION_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.ANNOTATION_SUBJECT);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getValue(), EdgeLabels.ANNOTATION_VALUE);
    verify(visitor).createNestedTranslation(axiom.getValue());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitNegativeObjectPropertyAssertion() {
    var axiom = mock(OWLNegativeObjectPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anySourceIndividual);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getObject()).thenReturn(anyTargetIndividual);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getObject());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitNegativeDataPropertyAssertion() {
    var axiom = mock(OWLNegativeDataPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anyIndividual);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getObject()).thenReturn(anyLiteral);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_VALUE);
    verify(visitor).createNestedTranslation(axiom.getObject());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitFunctionalObjectPropertyAxiom() {
    var axiom = mock(OWLFunctionalObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitInverseFunctionalObjectPropertyAxiom() {
    var axiom = mock(OWLInverseFunctionalObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitSynmetricObjectPropertyAxiom() {
    var axiom = mock(OWLSymmetricObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitAsymmetricObjectPropertyAxiom() {
    var axiom = mock(OWLAsymmetricObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitReflexiveObjectPropertyAxiom() {
    var axiom = mock(OWLReflexiveObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitIrreflexiveObjectPropertyAxiom() {
    var axiom = mock(OWLIrreflexiveObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitTransitiveObjectPropertyAxiom() {
    var axiom = mock(OWLTransitiveObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test
  public void shouldVisitFunctionalDataPropertyAxiom() {
    var axiom = mock(OWLFunctionalDataPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getAnnotations()).thenReturn(annotations);
    when(nodeIdMapper.get(axiom)).thenReturn(nodeId);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.FUNCTIONAL_DATA_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdges(axiom.getAnnotations(), EdgeLabels.AXIOM_ANNOTATION);
    verify(visitor).createNestedTranslations(axiom.getAnnotations());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenNodeIdMapperNull() {
    NodeIdMapper nullIdMapper = null;
    new AxiomVisitor(nullIdMapper, visitorFactory);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenVisitorFactoryNull() {
    VisitorFactory nullVisitorFactory = null;
    new AxiomVisitor(nodeIdMapper, nullVisitorFactory);
  }
}