package edu.stanford.owl2lpg.translator;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * A collection of node labels used to name the OWL 2 objects in the
 * specification.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NodeLabels {

  // @formatter:off
  public static final List<String> IRI = Lists.newArrayList("IRI");
  public static final List<String> CLASS = Lists.newArrayList("Class", "ClassExpression", "Entity");
  public static final List<String> DATATYPE = Lists.newArrayList("Datatype", "Entity");
  public static final List<String> OBJECT_PROPERTY = Lists.newArrayList("ObjectProperty", "ObjectPropertyExpression", "Entity");
  public static final List<String> DATA_PROPERTY = Lists.newArrayList("DataProperty", "DataPropertyExpression", "Entity");
  public static final List<String> ANNOTATION_PROPERTY = Lists.newArrayList("AnnotationProperty", "Entity");
  public static final List<String> NAMED_INDIVIDUAL = Lists.newArrayList("NamedIndividual", "Individual", "Entity");
  public static final List<String> ANONYMOUS_INDIVIDUAL = Lists.newArrayList("AnonymousIndividual", "Individual");

  public static final List<String> DECLARATION = Lists.newArrayList("Declaration", "Axiom");
  public static final List<String> SUBCLASSOF = Lists.newArrayList("SubClassOf", "ClassAxiom", "Axiom");

  public static final List<String> OBJECT_INTERSECTION_OF = Lists.newArrayList("ObjectIntersectionOf", "ClassExpression");
  public static final List<String> OBJECT_UNION_OF = Lists.newArrayList("ObjectUnionOf", "ClassExpression");
  public static final List<String> OBJECT_COMPLEMENT_OF = Lists.newArrayList("ObjectComplementOf", "ClassExpression");
  public static final List<String> OBJECT_SOME_VALUES_FROM = Lists.newArrayList("ObjectSomeValuesFrom", "ClassExpression");
  public static final List<String> OBJECT_ALL_VALUES_FROM = Lists.newArrayList("ObjectAllValuesFrom", "ClassExpression");
  public static final List<String> OBJECT_MIN_CARDINALITY = Lists.newArrayList("ObjectMinCardinality", "ClassExpression");
  public static final List<String> OBJECT_EXACT_CARDINALITY = Lists.newArrayList("ObjectExactCardinality", "ClassExpression");
  public static final List<String> OBJECT_MAX_CARDINALITY = Lists.newArrayList("ObjectMaxCardinality", "ClassExpression");

  public static final List<String> DATA_SOME_VALUES_FROM = Lists.newArrayList("DataSomeValuesFrom", "ClassExpression");
  public static final List<String> DATA_ALL_VALUES_FROM = Lists.newArrayList("DataAllValuesFrom", "ClassExpression");
  public static final List<String> DATA_MIN_CARDINALITY = Lists.newArrayList("DataMinCardinality", "ClassExpression");
  public static final List<String> DATA_EXACT_CARDINALITY = Lists.newArrayList("DataExactCardinality", "ClassExpression");
  public static final List<String> DATA_MAX_CARDINALITY = Lists.newArrayList("DataMaxCardinality", "ClassExpression");
  public static final List<String> OBJECT_HAS_VALUE = Lists.newArrayList("ObjectHasValue", "ClassExpression");
  public static final List<String> OBJECT_HAS_SELF = Lists.newArrayList("ObjectHasSelf", "ClassExpression");
  public static final List<String> OBJECT_ONE_OF = Lists.newArrayList("ObjectOneOf", "ClassExpression");

  public static final List<String> OBJECT_INVERSE_OF = Lists.newArrayList("ObjectInverseOf", "ObjectPropertyExpression");
  public static final List<String> DATA_HAS_VALUE = Lists.newArrayList("DataHasValue", "ClassExpression");



  // @formatter:on
}
