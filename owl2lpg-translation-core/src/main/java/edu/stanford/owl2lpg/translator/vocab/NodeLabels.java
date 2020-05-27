package edu.stanford.owl2lpg.translator.vocab;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A collection of node labels used to name the OWL 2 objects in the
 * specification.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum NodeLabels {

  // @formatter:off
  PROJECT(ImmutableList.of("Project")),
  BRANCH(ImmutableList.of("Branch")),
  ONTOLOGY_DOCUMENT(ImmutableList.of("OntologyDocument")),

  ONTOLOGY(ImmutableList.of("Ontology")),
  ONTOLOGY_ID(ImmutableList.of("OntologyID")),

  IRI(ImmutableList.of("IRI")),
  CLASS(ImmutableList.of("Class", "ClassExpression", "Entity")),
  DATATYPE(ImmutableList.of("Datatype", "DataRange", "Entity")),
  OBJECT_PROPERTY(ImmutableList.of("ObjectProperty", "ObjectPropertyExpression", "Entity")),
  DATA_PROPERTY(ImmutableList.of("DataProperty", "DataPropertyExpression", "Entity")),
  ANNOTATION_PROPERTY(ImmutableList.of("AnnotationProperty", "Entity")),
  NAMED_INDIVIDUAL(ImmutableList.of("NamedIndividual", "Individual", "Entity")),
  ANONYMOUS_INDIVIDUAL(ImmutableList.of("AnonymousIndividual", "Individual")),

  OBJECT_INTERSECTION_OF(ImmutableList.of("ObjectIntersectionOf", "ClassExpression")),
  OBJECT_UNION_OF(ImmutableList.of("ObjectUnionOf", "ClassExpression")),
  OBJECT_COMPLEMENT_OF(ImmutableList.of("ObjectComplementOf", "ClassExpression")),
  OBJECT_SOME_VALUES_FROM(ImmutableList.of("ObjectSomeValuesFrom", "ClassExpression")),
  OBJECT_ALL_VALUES_FROM(ImmutableList.of("ObjectAllValuesFrom", "ClassExpression")),
  OBJECT_MIN_CARDINALITY(ImmutableList.of("ObjectMinCardinality", "ClassExpression")),
  OBJECT_EXACT_CARDINALITY(ImmutableList.of("ObjectExactCardinality", "ClassExpression")),
  OBJECT_MAX_CARDINALITY(ImmutableList.of("ObjectMaxCardinality", "ClassExpression")),

  DATA_SOME_VALUES_FROM(ImmutableList.of("DataSomeValuesFrom", "ClassExpression")),
  DATA_ALL_VALUES_FROM(ImmutableList.of("DataAllValuesFrom", "ClassExpression")),
  DATA_MIN_CARDINALITY(ImmutableList.of("DataMinCardinality", "ClassExpression")),
  DATA_EXACT_CARDINALITY(ImmutableList.of("DataExactCardinality", "ClassExpression")),
  DATA_MAX_CARDINALITY(ImmutableList.of("DataMaxCardinality", "ClassExpression")),
  OBJECT_HAS_VALUE(ImmutableList.of("ObjectHasValue", "ClassExpression")),
  OBJECT_HAS_SELF(ImmutableList.of("ObjectHasSelf", "ClassExpression")),
  OBJECT_ONE_OF(ImmutableList.of("ObjectOneOf", "ClassExpression")),
  DATA_HAS_VALUE(ImmutableList.of("DataHasValue", "ClassExpression")),

  OBJECT_INVERSE_OF(ImmutableList.of("ObjectInverseOf", "ObjectPropertyExpression")),

  DATA_COMPLEMENT_OF(ImmutableList.of("DataComplementOf", "DataRange")),
  DATA_INTERSECTION_OF(ImmutableList.of("DataIntersectionOf", "DataRange")),
  DATA_UNION_OF(ImmutableList.of("DataUnionOf", "DataRange")),
  DATA_ONE_OF(ImmutableList.of("DataOneOf", "DataRange")),
  DATATYPE_RESTRICTION(ImmutableList.of("DatatypeRestriction", "DataRange")),
  FACET_RESTRICTION(ImmutableList.of("DatatypeRestriction", "DataRange")),

  LITERAL(ImmutableList.of("Literal")),
  LANGUAGE_TAG(ImmutableList.of("LanguageTag")),

  DECLARATION(ImmutableList.of("Declaration", "Axiom")),
  SUB_CLASS_OF(ImmutableList.of("SubClassOf", "ClassAxiom", "Axiom")),
  DATATYPE_DEFINITION(ImmutableList.of("DatatypeDefinition", "Axiom")),
  EQUIVALENT_CLASSES(ImmutableList.of("EquivalentClasses", "ClassAxiom", "Axiom")),
  DISJOINT_CLASSES(ImmutableList.of("DisjointClasses", "ClassAxiom", "Axiom")),
  DISJOINT_UNION(ImmutableList.of("DisjointUnion", "ClassAxiom", "Axiom")),
  SUB_OBJECT_PROPERTY_OF(ImmutableList.of("SubObjectPropertyOf", "ObjectPropertyAxiom", "Axiom")),
  EQUIVALENT_OBJECT_PROPERTIES(ImmutableList.of("EquivalentObjectProperties", "ObjectPropertyAxiom", "Axiom")),
  DISJOINT_OBJECT_PROPERTIES(ImmutableList.of("DisjointObjectProperties", "ObjectPropertyAxiom", "Axiom")),
  OBJECT_PROPERTY_DOMAIN(ImmutableList.of("ObjectPropertyDomain", "ObjectPropertyAxiom", "Axiom")),
  OBJECT_PROPERTY_RANGE(ImmutableList.of("ObjectPropertyRange", "ObjectPropertyAxiom", "Axiom")),
  INVERSE_OBJECT_PROPERTIES(ImmutableList.of("InverseObjectProperties", "ObjectPropertyAxiom", "Axiom")),
  FUNCTIONAL_OBJECT_PROPERTY(ImmutableList.of("FunctionalObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  INVERSE_FUNCTIONAL_OBJECT_PROPERTY(ImmutableList.of("InverseFunctionalObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  REFLEXIVE_OBJECT_PROPERTY(ImmutableList.of("ReflexiveObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  IRREFLEXIVE_OBJECT_PROPERTY(ImmutableList.of("IrreflexiveObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  SYMMETRIC_OBJECT_PROPERTY(ImmutableList.of("SymmetricObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  ASYMMETRIC_OBJECT_PROPERTY(ImmutableList.of("AsymmetricObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  TRANSITIVE_OBJECT_PROPERTY(ImmutableList.of("TransitiveObjectProperty", "ObjectPropertyAxiom", "Axiom")),
  SUB_DATA_PROPERTY_OF(ImmutableList.of("SubDataPropertyOf", "DataPropertyAxiom", "Axiom")),
  EQUIVALENT_DATA_PROPERTIES(ImmutableList.of("EquivalentDataProperties", "DataPropertyAxiom", "Axiom")),
  DISJOINT_DATA_PROPERTIES(ImmutableList.of("DisjointDataProperties", "DataPropertyAxiom", "Axiom")),
  DATA_PROPERTY_DOMAIN(ImmutableList.of("DataPropertyDomain", "DataPropertyAxiom", "Axiom")),
  DATA_PROPERTY_RANGE(ImmutableList.of("DataPropertyRange", "DataPropertyAxiom", "Axiom")),
  FUNCTIONAL_DATA_PROPERTY(ImmutableList.of("FunctionalDataProperty", "DataPropertyAxiom", "Axiom")),
  HAS_KEY(ImmutableList.of("HasKey", "Axiom")),
  SAME_INDIVIDUAL(ImmutableList.of("SameIndividual", "Assertion", "Axiom")),
  DIFFERENT_INDIVIDUALS(ImmutableList.of("DifferentIndividuals", "Assertion", "Axiom")),
  CLASS_ASSERTION(ImmutableList.of("ClassAssertion", "Assertion", "Axiom")),
  OBJECT_PROPERTY_ASSERTION(ImmutableList.of("ObjectPropertyAssertion", "Assertion", "Axiom")),
  NEGATIVE_OBJECT_PROPERTY_ASSERTION(ImmutableList.of("NegativeObjectPropertyAssertion", "Assertion", "Axiom")),
  DATA_PROPERTY_ASSERTION(ImmutableList.of("DataPropertyAssertion", "Assertion", "Axiom")),
  NEGATIVE_DATA_PROPERTY_ASSERTION(ImmutableList.of("NegativeDataPropertyAssertion", "Assertion", "Axiom")),
  ANNOTATION_ASSERTION(ImmutableList.of("AnnotationAssertion", "AnnotationAxiom", "Axiom")),
  SUB_ANNOTATION_PROPERTY_OF(ImmutableList.of("SubAnnotationPropertyOf", "AnnotationAxiom", "Axiom")),
  ANNOTATION_PROPERTY_DOMAIN(ImmutableList.of("AnnotationPropertyDomain", "AnnotationAxiom", "Axiom")),
  ANNOTATION_PROPERTY_RANGE(ImmutableList.of("AnnotationPropertyRange", "AnnotationAxiom", "Axiom")),
  ANNOTATION(ImmutableList.of("Annotation")),
  SWRL_RULE(ImmutableList.of("SWRLRule"));
  // @formatter:on

  @Nonnull
  private final ImmutableList<String> values;

  @Nonnull
  private final String printLabel;

  NodeLabels(@Nonnull ImmutableList<String> values) {
    this.values = checkNotNull(values);
    this.printLabel = values.stream()
        .collect(Collectors.joining(":", ":", ""));
  }

  @Nonnull
  public ImmutableList<String> getValues() {
    return values;
  }

  @Nonnull
  public String printLabels() {
    return printLabel;
  }
}