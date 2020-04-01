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
  public static final ImmutableList<String> IRI = ImmutableList.of("IRI");
  public static final ImmutableList<String> CLASS = ImmutableList.of("Class", "ClassExpression", "Entity");
  public static final ImmutableList<String> DATATYPE = ImmutableList.of("Datatype", "Entity");
  public static final ImmutableList<String> OBJECT_PROPERTY = ImmutableList.of("ObjectProperty", "ObjectPropertyExpression", "Entity");
  public static final ImmutableList<String> DATA_PROPERTY = ImmutableList.of("DataProperty", "DataPropertyExpression", "Entity");
  public static final ImmutableList<String> ANNOTATION_PROPERTY = ImmutableList.of("AnnotationProperty", "Entity");
  public static final ImmutableList<String> NAMED_INDIVIDUAL = ImmutableList.of("NamedIndividual", "Individual", "Entity");
  public static final ImmutableList<String> ANONYMOUS_INDIVIDUAL = ImmutableList.of("AnonymousIndividual", "Individual");

  public static final ImmutableList<String> DECLARATION = ImmutableList.of("Declaration", "Axiom");
  public static final ImmutableList<String> SUBCLASSOF = ImmutableList.of("SubClassOf", "ClassAxiom", "Axiom");

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
  // @formatter:on
}