package edu.stanford.owl2lpg.translator.vocab;

import com.google.common.collect.ImmutableList;

/**
 * A collection of node labels used to name the OWL 2 objects in the
 * specification.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeLabels {

  // @formatter:off
  public static final ImmutableList<String> PROJECT = ImmutableList.of("Project");
  public static final ImmutableList<String> BRANCH = ImmutableList.of("Branch");
  public static final ImmutableList<String> ONTOLOGY_DOCUMENT = ImmutableList.of("OntologyDocument");

  public static final ImmutableList<String> ONTOLOGY = ImmutableList.of("Ontology");
  public static final ImmutableList<String> ONTOLOGY_ID = ImmutableList.of("OntologyID");

  public static final ImmutableList<String> IRI = ImmutableList.of("IRI");
  public static final ImmutableList<String> CLASS = ImmutableList.of("Class", "ClassExpression", "Entity");
  public static final ImmutableList<String> DATATYPE = ImmutableList.of("Datatype", "DataRange", "Entity");
  public static final ImmutableList<String> OBJECT_PROPERTY = ImmutableList.of("ObjectProperty", "ObjectPropertyExpression", "Entity");
  public static final ImmutableList<String> DATA_PROPERTY = ImmutableList.of("DataProperty", "DataPropertyExpression", "Entity");
  public static final ImmutableList<String> ANNOTATION_PROPERTY = ImmutableList.of("AnnotationProperty", "Entity");
  public static final ImmutableList<String> NAMED_INDIVIDUAL = ImmutableList.of("NamedIndividual", "Individual", "Entity");
  public static final ImmutableList<String> ANONYMOUS_INDIVIDUAL = ImmutableList.of("AnonymousIndividual", "Individual");

  public static final ImmutableList<String> OBJECT_INTERSECTION_OF = ImmutableList.of("ObjectIntersectionOf", "ClassExpression");
  public static final ImmutableList<String> OBJECT_UNION_OF = ImmutableList.of("ObjectUnionOf", "ClassExpression");
  public static final ImmutableList<String> OBJECT_COMPLEMENT_OF = ImmutableList.of("ObjectComplementOf", "ClassExpression");
  public static final ImmutableList<String> OBJECT_SOME_VALUES_FROM = ImmutableList.of("ObjectSomeValuesFrom", "ClassExpression");
  public static final ImmutableList<String> OBJECT_ALL_VALUES_FROM = ImmutableList.of("ObjectAllValuesFrom", "ClassExpression");
  public static final ImmutableList<String> OBJECT_MIN_CARDINALITY = ImmutableList.of("ObjectMinCardinality", "ClassExpression");
  public static final ImmutableList<String> OBJECT_EXACT_CARDINALITY = ImmutableList.of("ObjectExactCardinality", "ClassExpression");
  public static final ImmutableList<String> OBJECT_MAX_CARDINALITY = ImmutableList.of("ObjectMaxCardinality", "ClassExpression");

  public static final ImmutableList<String> DATA_SOME_VALUES_FROM = ImmutableList.of("DataSomeValuesFrom", "ClassExpression");
  public static final ImmutableList<String> DATA_ALL_VALUES_FROM = ImmutableList.of("DataAllValuesFrom", "ClassExpression");
  public static final ImmutableList<String> DATA_MIN_CARDINALITY = ImmutableList.of("DataMinCardinality", "ClassExpression");
  public static final ImmutableList<String> DATA_EXACT_CARDINALITY = ImmutableList.of("DataExactCardinality", "ClassExpression");
  public static final ImmutableList<String> DATA_MAX_CARDINALITY = ImmutableList.of("DataMaxCardinality", "ClassExpression");
  public static final ImmutableList<String> OBJECT_HAS_VALUE = ImmutableList.of("ObjectHasValue", "ClassExpression");
  public static final ImmutableList<String> OBJECT_HAS_SELF = ImmutableList.of("ObjectHasSelf", "ClassExpression");
  public static final ImmutableList<String> OBJECT_ONE_OF = ImmutableList.of("ObjectOneOf", "ClassExpression");
  public static final ImmutableList<String> DATA_HAS_VALUE = ImmutableList.of("DataHasValue", "ClassExpression");

  public static final ImmutableList<String> OBJECT_INVERSE_OF = ImmutableList.of("ObjectInverseOf", "ObjectPropertyExpression");

  public static final ImmutableList<String> DATA_COMPLEMENT_OF = ImmutableList.of("DataComplementOf", "DataRange");
  public static final ImmutableList<String> DATA_INTERSECTION_OF = ImmutableList.of("DataIntersectionOf", "DataRange");
  public static final ImmutableList<String> DATA_UNION_OF = ImmutableList.of("DataUnionOf", "DataRange");
  public static final ImmutableList<String> DATA_ONE_OF = ImmutableList.of("DataOneOf", "DataRange");
  public static final ImmutableList<String> DATATYPE_RESTRICTION = ImmutableList.of("DatatypeRestriction", "DataRange");
  public static final ImmutableList<String> FACET_RESTRICTION = ImmutableList.of("DatatypeRestriction", "DataRange");

  public static final ImmutableList<String> LITERAL = ImmutableList.of("Literal");
  public static final ImmutableList<String> LANGUAGE_TAG = ImmutableList.of("LanguageTag");

  public static final ImmutableList<String> DECLARATION = ImmutableList.of("Declaration", "Axiom");
  public static final ImmutableList<String> SUB_CLASS_OF = ImmutableList.of("SubClassOf", "ClassAxiom", "Axiom");
  public static final ImmutableList<String> DATATYPE_DEFINITION = ImmutableList.of("DatatypeDefinition", "Axiom");
  public static final ImmutableList<String> EQUIVALENT_CLASSES = ImmutableList.of("EquivalentClasses", "ClassAxiom", "Axiom");
  public static final ImmutableList<String> DISJOINT_CLASSES = ImmutableList.of("DisjointClasses", "ClassAxiom", "Axiom");
  public static final ImmutableList<String> DISJOINT_UNION = ImmutableList.of("DisjointUnion", "ClassAxiom", "Axiom");
  public static final ImmutableList<String> SUB_OBJECT_PROPERTY_OF = ImmutableList.of("SubObjectPropertyOf", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> EQUIVALENT_OBJECT_PROPERTIES = ImmutableList.of("EquivalentObjectProperties", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> DISJOINT_OBJECT_PROPERTIES = ImmutableList.of("DisjointObjectProperties", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> OBJECT_PROPERTY_DOMAIN = ImmutableList.of("ObjectPropertyDomain", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> OBJECT_PROPERTY_RANGE = ImmutableList.of("ObjectPropertyRange", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> INVERSE_OBJECT_PROPERTIES = ImmutableList.of("InverseObjectProperties", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> FUNCTIONAL_OBJECT_PROPERTY = ImmutableList.of("FunctionalObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> INVERSE_FUNCTIONAL_OBJECT_PROPERTY = ImmutableList.of("InverseFunctionalObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> REFLEXIVE_OBJECT_PROPERTY = ImmutableList.of("ReflexiveObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> IRREFLEXIVE_OBJECT_PROPERTY = ImmutableList.of("IrreflexiveObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> SYMMETRIC_OBJECT_PROPERTY = ImmutableList.of("SymmetricObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> ASYMMETRIC_OBJECT_PROPERTY = ImmutableList.of("AsymmetricObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> TRANSITIVE_OBJECT_PROPERTY = ImmutableList.of("TransitiveObjectProperty", "ObjectPropertyAxiom", "Axiom");
  public static final ImmutableList<String> SUB_DATA_PROPERTY_OF = ImmutableList.of("SubDataPropertyOf", "DataPropertyAxiom", "Axiom");
  public static final ImmutableList<String> EQUIVALENT_DATA_PROPERTIES = ImmutableList.of("EquivalentDataProperties", "DataPropertyAxiom", "Axiom");
  public static final ImmutableList<String> DISJOINT_DATA_PROPERTIES = ImmutableList.of("DisjointDataProperties", "DataPropertyAxiom", "Axiom");
  public static final ImmutableList<String> DATA_PROPERTY_DOMAIN = ImmutableList.of("DataPropertyDomain", "DataPropertyAxiom", "Axiom");
  public static final ImmutableList<String> DATA_PROPERTY_RANGE = ImmutableList.of("DataPropertyRange", "DataPropertyAxiom", "Axiom");
  public static final ImmutableList<String> FUNCTIONAL_DATA_PROPERTY = ImmutableList.of("FunctionalDataProperty", "DataPropertyAxiom", "Axiom");
  public static final ImmutableList<String> HAS_KEY = ImmutableList.of("HasKey", "Axiom");
  public static final ImmutableList<String> SAME_INDIVIDUAL = ImmutableList.of("SameIndividual", "Assertion", "Axiom");
  public static final ImmutableList<String> DIFFERENT_INDIVIDUALS = ImmutableList.of("DifferentIndividuals", "Assertion", "Axiom");
  public static final ImmutableList<String> CLASS_ASSERTION = ImmutableList.of("ClassAssertion", "Assertion", "Axiom");
  public static final ImmutableList<String> OBJECT_PROPERTY_ASSERTION = ImmutableList.of("ObjectPropertyAssertion", "Assertion", "Axiom");
  public static final ImmutableList<String> NEGATIVE_OBJECT_PROPERTY_ASSERTION = ImmutableList.of("NegativeObjectPropertyAssertion", "Assertion", "Axiom");
  public static final ImmutableList<String> DATA_PROPERTY_ASSERTION = ImmutableList.of("DataPropertyAssertion", "Assertion", "Axiom");
  public static final ImmutableList<String> NEGATIVE_DATA_PROPERTY_ASSERTION = ImmutableList.of("NegativeDataPropertyAssertion", "Assertion", "Axiom");
  public static final ImmutableList<String> ANNOTATION_ASSERTION = ImmutableList.of("AnnotationAssertion", "AnnotationAxiom", "Axiom");
  public static final ImmutableList<String> SUB_ANNOTATION_PROPERTY_OF = ImmutableList.of("SubAnnotationPropertyOf", "AnnotationAxiom", "Axiom");
  public static final ImmutableList<String> ANNOTATION_PROPERTY_DOMAIN = ImmutableList.of("AnnotationPropertyDomain", "AnnotationAxiom", "Axiom");
  public static final ImmutableList<String> ANNOTATION_PROPERTY_RANGE = ImmutableList.of("AnnotationPropertyRange", "AnnotationAxiom", "Axiom");
  public static final ImmutableList<String> ANNOTATION = ImmutableList.of("Annotation");
  // @formatter:on
}