package edu.stanford.owl2lpg.translator.visitors;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    EntityVisitorTest.class,
    ClassExpressionVisitorTest.class,
    PropertyExpressionVisitorTest.class,
    IndividualVisitorTest.class,
    DataVisitorTest.class,
    AnnotationSubjectVisitorTest.class,
    AnnotationValueVisitorTest.class,
    AnnotationObjectVisitorTest.class,
    AxiomVisitorTest.class
})
public class VisitorTestSuite {
  // NO-OP
}
