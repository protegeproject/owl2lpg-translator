package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.*;
import org.semanticweb.owlapi.model.*;

public class TranslatorFactory {

  private static OWLAxiomVisitorEx<Translation> axiomVisitor;
  private static OWLClassExpressionVisitorEx<Translation> classExpressionVisitor;
  private static OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor;
  private static OWLEntityVisitorEx<Translation> entityVisitor;
  private static OWLIndividualVisitorEx<Translation> individualVisitor;
  private static OWLDataVisitorEx<Translation> dataVisitor;
  private static OWLAnnotationSubjectVisitorEx<Translation> annotationSubjectVisitor;
  private static OWLAnnotationValueVisitorEx<Translation> annotationValueVisitor;

  static {
    entityVisitor = new EntityVisitor();
    propertyExpressionVisitor = new PropertyExpressionVisitor(entityVisitor);
    individualVisitor = new IndividualVisitor(entityVisitor);
    dataVisitor = new DataVisitor(entityVisitor);
    classExpressionVisitor = new ClassExpressionVisitor(
        entityVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor);
    annotationSubjectVisitor = new AnnotationSubjectVisitor(individualVisitor);
    annotationValueVisitor = new AnnotationValueVisitor(
        dataVisitor,
        individualVisitor);
    axiomVisitor = new AxiomVisitor(entityVisitor,
        propertyExpressionVisitor,
        individualVisitor,
        dataVisitor,
        classExpressionVisitor,
        annotationSubjectVisitor,
        annotationValueVisitor);
  }

  public static AxiomTranslator getAxiomTranslator() {
    return new AxiomTranslator(axiomVisitor);
  }

  public static ClassExpressionTranslator getClassExpressionTranslator() {
    return new ClassExpressionTranslator(classExpressionVisitor);
  }

  public static PropertyExpressionTranslator getPropertyExpressionTranslator() {
    return new PropertyExpressionTranslator(propertyExpressionVisitor);
  }

  public static EntityTranslator getEntityTranslator() {
    return new EntityTranslator(entityVisitor);
  }

  public static IndividualTranslator getIndividualTranslator() {
    return new IndividualTranslator(individualVisitor);
  }

  public static DataRangeTranslator getDataRangeTranslator() {
    return new DataRangeTranslator(dataVisitor);
  }

  public static LiteralTranslator getLiteralTranslator() {
    return new LiteralTranslator(dataVisitor);
  }
}
