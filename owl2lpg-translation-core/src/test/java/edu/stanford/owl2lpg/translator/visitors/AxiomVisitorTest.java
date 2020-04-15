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

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AxiomVisitorTest {

  private AxiomVisitor visitor;

  // @formatter:off
  @Mock private EntityVisitor entityVisitor;
  @Mock private ClassExpressionVisitor classExpressionVisitor;
  @Mock private PropertyExpressionVisitor propertyExpressionVisitor;
  @Mock private IndividualVisitor individualVisitor;
  @Mock private DataVisitor dataVisitor;
  @Mock private AnnotationSubjectVisitor annotationSubjectVisitor;
  @Mock private AnnotationValueVisitor annotationValueVisitor;

  @Mock private OWLAxiom anyAxiom;
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
  @Mock private OWLAnnotationSubject anyAnnotationSubject;
  @Mock private OWLAnnotationValue anyAnnotationValue;

  @Mock private Translation nestedTranslation;
  @Mock private Node nestedTranslationMainNode;
  // @formatter:off

  @Before
  public void setUp() throws Exception {
    visitor = spy(new AxiomVisitor(entityVisitor,
        classExpressionVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor));
    when(anyClass.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(anyDatatype.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(anyAnnotationProperty.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(anySubAnnotationProperty.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(anySuperAnnotationProperty.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(anyEntity.accept(entityVisitor)).thenReturn(nestedTranslation);
    when(anyClassExpression.accept(classExpressionVisitor)).thenReturn(nestedTranslation);
    when(anySubClassExpression.accept(classExpressionVisitor)).thenReturn(nestedTranslation);
    when(anySuperClassExpression.accept(classExpressionVisitor)).thenReturn(nestedTranslation);
    when(anyObjectPropertyExpression.accept(propertyExpressionVisitor)).thenReturn(nestedTranslation);
    when(anySubObjectPropertyExpression.accept(propertyExpressionVisitor)).thenReturn(nestedTranslation);
    when(anySuperObjectPropertyExpression.accept(propertyExpressionVisitor)).thenReturn(nestedTranslation);
    when(anyDataPropertyExpression.accept(propertyExpressionVisitor)).thenReturn(nestedTranslation);
    when(anySubDataPropertyExpression.accept(propertyExpressionVisitor)).thenReturn(nestedTranslation);
    when(anySuperDataPropertyExpression.accept(propertyExpressionVisitor)).thenReturn(nestedTranslation);
    when(anyIndividual.accept(individualVisitor)).thenReturn(nestedTranslation);
    when(anySourceIndividual.accept(individualVisitor)).thenReturn(nestedTranslation);
    when(anyTargetIndividual.accept(individualVisitor)).thenReturn(nestedTranslation);
    when(anyDataRange.accept(dataVisitor)).thenReturn(nestedTranslation);
    when(anyLiteral.accept(dataVisitor)).thenReturn(nestedTranslation);
    when(anyIri.accept(annotationSubjectVisitor)).thenReturn(nestedTranslation);
    when(anyAnnotationSubject.accept(annotationSubjectVisitor)).thenReturn(nestedTranslation);
    when(anyAnnotationValue.accept(annotationValueVisitor)).thenReturn(nestedTranslation);
    when(nestedTranslation.getMainNode()).thenReturn(nestedTranslationMainNode);
  }

  @Test
  public void shouldVisitDeclarationAxiom() {
    var axiom = mock(OWLDeclarationAxiom.class);
    when(axiom.getEntity()).thenReturn(anyEntity);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DECLARATION);
    verify(visitor).createEdge(axiom.getEntity(), EdgeLabels.ENTITY);
    verify(visitor).createNestedTranslation(axiom.getEntity());
  }

  @Test
  public void shouldVisitDatatypeDefinitionAxiom() {
    var axiom = mock(OWLDatatypeDefinitionAxiom.class);
    when(axiom.getDatatype()).thenReturn(anyDatatype);
    when(axiom.getDataRange()).thenReturn(anyDataRange);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATATYPE_DEFINITION);
    verify(visitor).createEdge(axiom.getDatatype(), EdgeLabels.DATATYPE);
    verify(visitor).createNestedTranslation(axiom.getDatatype());
    verify(visitor).createEdge(axiom.getDataRange(), EdgeLabels.DATA_RANGE);
    verify(visitor).createNestedTranslation(axiom.getDataRange());
  }

  @Test
  public void shouldVisitSubClassOfAxiom() {
    var axiom = mock(OWLSubClassOfAxiom.class);
    when(axiom.getSubClass()).thenReturn(anySubClassExpression);
    when(axiom.getSuperClass()).thenReturn(anySuperClassExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_CLASS_OF);
    verify(visitor).createEdge(axiom.getSubClass(), EdgeLabels.SUB_CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSubClass());
    verify(visitor).createEdge(axiom.getSuperClass(), EdgeLabels.SUPER_CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSuperClass());
  }

  @Test
  public void shouldVisitSubObjectPropertyOfAxiom() {
    var axiom = mock(OWLSubObjectPropertyOfAxiom.class);
    when(axiom.getSubProperty()).thenReturn(anySubObjectPropertyExpression);
    when(axiom.getSuperProperty()).thenReturn(anySuperObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getSubProperty(), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSubProperty());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSuperProperty());
  }

  @Test
  public void shouldVisitSubDataPropertyOfAxiom() {
    var axiom = mock(OWLSubDataPropertyOfAxiom.class);
    when(axiom.getSubProperty()).thenReturn(anySubDataPropertyExpression);
    when(axiom.getSuperProperty()).thenReturn(anySuperDataPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_DATA_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getSubProperty(), EdgeLabels.SUB_DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSubProperty());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSuperProperty());
  }

  @Test
  public void shouldVisitSubPropertyChainOfAxiom() {
    var axiom = mock(OWLSubPropertyChainOfAxiom.class);
    when(axiom.getPropertyChain()).thenReturn(listOfObjectPropertyExpressions);
    when(axiom.getPropertyChain().get(0)).thenReturn(anyObjectPropertyExpression);
    when(axiom.getPropertyChain().size()).thenReturn(1);
    when(axiom.getSuperProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_OBJECT_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getPropertyChain().get(0), EdgeLabels.SUB_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createChainTranslation(axiom.getPropertyChain());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_OBJECT_PROPERTY_EXPRESSION);
    verify(visitor, times(2)).createNestedTranslation(axiom.getSuperProperty());
  }

  @Test
  public void shouldVisitSubAnnotationPropertyOfAxiom() {
    var axiom = mock(OWLSubAnnotationPropertyOfAxiom.class);
    when(axiom.getSubProperty()).thenReturn(anySubAnnotationProperty);
    when(axiom.getSuperProperty()).thenReturn(anySuperAnnotationProperty);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SUB_ANNOTATION_PROPERTY_OF);
    verify(visitor).createEdge(axiom.getSubProperty(), EdgeLabels.SUB_ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getSubProperty());
    verify(visitor).createEdge(axiom.getSuperProperty(), EdgeLabels.SUPER_ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getSuperProperty());
  }

  @Test
  public void shouldVisitObjectPropertyDomainAxiom() {
    var axiom = mock(OWLObjectPropertyDomainAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getDomain()).thenReturn(anyClassExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.OBJECT_PROPERTY_DOMAIN);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    verify(visitor).createNestedTranslation(axiom.getDomain());
  }

  @Test
  public void shouldVisitObjectPropertyRangeAxiom() {
    var axiom = mock(OWLObjectPropertyRangeAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getRange()).thenReturn(anyClassExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.OBJECT_PROPERTY_RANGE);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getRange(), EdgeLabels.RANGE);
    verify(visitor).createNestedTranslation(axiom.getRange());
  }

  @Test
  public void shouldVisitDataPropertyDomainAxiom() {
    var axiom = mock(OWLDataPropertyDomainAxiom.class);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getDomain()).thenReturn(anyClassExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATA_PROPERTY_DOMAIN);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    verify(visitor).createNestedTranslation(axiom.getDomain());
  }

  @Test
  public void shouldVisitDataPropertyRangeAxiom() {
    var axiom = mock(OWLDataPropertyRangeAxiom.class);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getRange()).thenReturn(anyDataRange);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATA_PROPERTY_RANGE);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getRange(), EdgeLabels.RANGE);
    verify(visitor).createNestedTranslation(axiom.getRange());
  }

  @Test
  public void shouldVisitAnnotationPropertyDomainAxiom() {
    var axiom = mock(OWLAnnotationPropertyDomainAxiom.class);
    when(axiom.getProperty()).thenReturn(anyAnnotationProperty);
    when(axiom.getDomain()).thenReturn(anyIri);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_DOMAIN);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getDomain(), EdgeLabels.DOMAIN);
    verify(visitor).createNestedTranslation(axiom.getDomain());
  }

  @Test
  public void shouldVisitAnnotationPropertyRangeAxiom() {
    var axiom = mock(OWLAnnotationPropertyRangeAxiom.class);
    when(axiom.getProperty()).thenReturn(anyAnnotationProperty);
    when(axiom.getRange()).thenReturn(anyIri);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ANNOTATION_PROPERTY_RANGE);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getRange(), EdgeLabels.RANGE);
    verify(visitor).createNestedTranslation(axiom.getRange());
  }

  @Test
  public void shouldVisitEquivalentClassesAxiom() {
    var axiom = mock(OWLEquivalentClassesAxiom.class);
    when(axiom.getClassExpressions()).thenReturn(classExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.EQUIVALENT_CLASSES);
    verify(visitor).createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getClassExpressions());
  }

  @Test
  public void shouldVisitEquivalentObjectPropertiesAxiom() {
    var axiom = mock(OWLEquivalentObjectPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(objectPropertyExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.EQUIVALENT_OBJECT_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
  }

  @Test
  public void shouldVisitEquivalentDataPropertiesAxiom() {
    var axiom = mock(OWLEquivalentDataPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(dataPropertyExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.EQUIVALENT_DATA_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
  }

  @Test
  public void shouldVisitDisjointClassesAxiom() {
    var axiom = mock(OWLDisjointClassesAxiom.class);
    when(axiom.getClassExpressions()).thenReturn(classExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_CLASSES);
    verify(visitor).createEdges(axiom.getClassExpressions(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getClassExpressions());
  }

  @Test
  public void shouldVisitDisjointUnionAxiom() {
    var axiom = mock(OWLDisjointUnionAxiom.class);
    when(axiom.getOWLClass()).thenReturn(anyClass);
    when(axiom.getClassExpressions()).thenReturn(classExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_UNION);
    verify(visitor).createEdge(axiom.getOWLClass(), EdgeLabels.CLASS);
    verify(visitor).createNestedTranslation(axiom.getOWLClass());
    verify(visitor).createEdges(axiom.getClassExpressions(), EdgeLabels.DISJOINT_CLASS_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getClassExpressions());
  }

  @Test
  public void shouldVisitDisjointObjectPropertiesAxiom() {
    var axiom = mock(OWLDisjointObjectPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(objectPropertyExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_OBJECT_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
  }

  @Test
  public void shouldVisitDisjointDataPropertiesAxiom() {
    var axiom = mock(OWLDisjointDataPropertiesAxiom.class);
    when(axiom.getProperties()).thenReturn(dataPropertyExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DISJOINT_DATA_PROPERTIES);
    verify(visitor).createEdges(axiom.getProperties(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getProperties());
  }

  @Test
  public void shouldVisitSameIndividualAxiom() {
    var axiom = mock(OWLSameIndividualAxiom.class);
    when(axiom.getIndividuals()).thenReturn(individuals);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SAME_INDIVIDUAL);
    verify(visitor).createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslations(axiom.getIndividuals());
  }

  @Test
  public void shouldVisitDifferentIndividualsAxiom() {
    var axiom = mock(OWLDifferentIndividualsAxiom.class);
    when(axiom.getIndividuals()).thenReturn(individuals);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DIFFERENT_INDIVIDUALS);
    verify(visitor).createEdges(axiom.getIndividuals(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslations(axiom.getIndividuals());
  }

  @Test
  public void shouldVisitInverseObjectPropertiesAxiom() {
    var axiom = mock(OWLInverseObjectPropertiesAxiom.class);
    when(axiom.getFirstProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getSecondProperty()).thenReturn(anySuperObjectPropertyExpression); // any ope

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.INVERSE_OBJECT_PROPERTIES);
    verify(visitor).createEdge(axiom.getFirstProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getFirstProperty());
    verify(visitor).createEdge(axiom.getSecondProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getSecondProperty());
  }

  @Test
  public void shouldVisitHasKeyAxiom() {
    var axiom = mock(OWLHasKeyAxiom.class);
    when(axiom.getClassExpression()).thenReturn(anyClassExpression);
    when(axiom.getObjectPropertyExpressions()).thenReturn(objectPropertyExpressions);
    when(axiom.getDataPropertyExpressions()).thenReturn(dataPropertyExpressions);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.HAS_KEY);
    verify(visitor).createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getClassExpression());
    verify(visitor).createEdges(axiom.getObjectPropertyExpressions(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getObjectPropertyExpressions());
    verify(visitor).createEdges(axiom.getDataPropertyExpressions(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslations(axiom.getDataPropertyExpressions());
  }

  @Test
  public void shouldVisitClassAssertionAxiom() {
    var axiom = mock(OWLClassAssertionAxiom.class);
    when(axiom.getClassExpression()).thenReturn(anyClassExpression);
    when(axiom.getIndividual()).thenReturn(anyIndividual);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.CLASS_ASSERTION);
    verify(visitor).createEdge(axiom.getClassExpression(), EdgeLabels.CLASS_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getClassExpression());
    verify(visitor).createEdge(axiom.getIndividual(), EdgeLabels.INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getIndividual());
  }

  @Test
  public void shouldVisitObjectPropertyAssertionAxiom() {
    var axiom = mock(OWLObjectPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anySourceIndividual);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getObject()).thenReturn(anyTargetIndividual);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.OBJECT_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getObject());
  }

  @Test
  public void shouldVisitDataPropertyAssertionAxiom() {
    var axiom = mock(OWLDataPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anyIndividual);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getObject()).thenReturn(anyLiteral);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.DATA_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_VALUE);
    verify(visitor).createNestedTranslation(axiom.getObject());
  }

  @Test
  public void shouldVisitAnnotationAssertionAxiom() {
    var axiom = mock(OWLAnnotationAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anyAnnotationSubject);
    when(axiom.getProperty()).thenReturn(anyAnnotationProperty);
    when(axiom.getValue()).thenReturn(anyAnnotationValue);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ANNOTATION_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.ANNOTATION_SUBJECT);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.ANNOTATION_PROPERTY);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getValue(), EdgeLabels.ANNOTATION_VALUE);
    verify(visitor).createNestedTranslation(axiom.getValue());
  }

  @Test
  public void shouldVisitNegativeObjectPropertyAssertion() {
    var axiom = mock(OWLNegativeObjectPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anySourceIndividual);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);
    when(axiom.getObject()).thenReturn(anyTargetIndividual);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.NEGATIVE_OBJECT_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getObject());
  }

  @Test
  public void shouldVisitNegativeDataPropertyAssertion() {
    var axiom = mock(OWLNegativeDataPropertyAssertionAxiom.class);
    when(axiom.getSubject()).thenReturn(anyIndividual);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);
    when(axiom.getObject()).thenReturn(anyLiteral);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.NEGATIVE_DATA_PROPERTY_ASSERTION);
    verify(visitor).createEdge(axiom.getSubject(), EdgeLabels.SOURCE_INDIVIDUAL);
    verify(visitor).createNestedTranslation(axiom.getSubject());
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
    verify(visitor).createEdge(axiom.getObject(), EdgeLabels.TARGET_VALUE);
    verify(visitor).createNestedTranslation(axiom.getObject());
  }

  @Test
  public void shouldVisitFunctionalObjectPropertyAxiom() {
    var axiom = mock(OWLFunctionalObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.FUNCTIONAL_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitInverseFunctionalObjectPropertyAxiom() {
    var axiom = mock(OWLInverseFunctionalObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitSynmetricObjectPropertyAxiom() {
    var axiom = mock(OWLSymmetricObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.SYMMETRIC_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitAsymmetricObjectPropertyAxiom() {
    var axiom = mock(OWLAsymmetricObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.ASYMMETRIC_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitReflexiveObjectPropertyAxiom() {
    var axiom = mock(OWLReflexiveObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.REFLEXIVE_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitIrreflexiveObjectPropertyAxiom() {
    var axiom = mock(OWLIrreflexiveObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.IRREFLEXIVE_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitTransitiveObjectPropertyAxiom() {
    var axiom = mock(OWLTransitiveObjectPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyObjectPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.TRANSITIVE_OBJECT_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.OBJECT_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test
  public void shouldVisitFunctionalDataPropertyAxiom() {
    var axiom = mock(OWLFunctionalDataPropertyAxiom.class);
    when(axiom.getProperty()).thenReturn(anyDataPropertyExpression);

    visitor.visit(axiom);
    verify(visitor).visit(axiom);
    verify(visitor).createNode(axiom, NodeLabels.FUNCTIONAL_DATA_PROPERTY);
    verify(visitor).createEdge(axiom.getProperty(), EdgeLabels.DATA_PROPERTY_EXPRESSION);
    verify(visitor).createNestedTranslation(axiom.getProperty());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenEntityVisitorNull() {
    EntityVisitor nullEntityVisitor = null;
    new AxiomVisitor(nullEntityVisitor,
        classExpressionVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenClassExpressionVisitorNull() {
    ClassExpressionVisitor nullClassExpressionVisitor = null;
    new AxiomVisitor(entityVisitor,
        nullClassExpressionVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenPropertyExpressionVisitorNull() {
    PropertyExpressionVisitor nullPropertyExpressionVisitor = null;
    new AxiomVisitor(entityVisitor,
        classExpressionVisitor,
        nullPropertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenIndividualVisitorNull() {
    IndividualVisitor nullIndividualVisitor = null;
    new AxiomVisitor(entityVisitor,
        classExpressionVisitor,
        propertyExpressionVisitor,
        nullIndividualVisitor,
        dataVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenDataVisitorNull() {
    DataVisitor nullDataVisitor = null;
    new AxiomVisitor(entityVisitor,
        classExpressionVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        nullDataVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenAnnotationSubjectVisitorNull() {
    AnnotationSubjectVisitor nullAnnotationSubjectVisitor = null;
    new AxiomVisitor(entityVisitor,
        classExpressionVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        nullAnnotationSubjectVisitor,
        annotationValueVisitor);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNPEWhenAnnotationValueVisitorNull() {
    AnnotationValueVisitor nullAnnotationValueVisitor = null;
    new AxiomVisitor(entityVisitor,
        classExpressionVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        annotationSubjectVisitor,
        nullAnnotationValueVisitor);
  }
}