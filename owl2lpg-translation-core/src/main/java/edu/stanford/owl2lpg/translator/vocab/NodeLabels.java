package edu.stanford.owl2lpg.translator.vocab;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.Stream;

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
  PROJECT("Project"),
  BRANCH("Branch"),
  ONTOLOGY_DOCUMENT("OntologyDocument"),

  ONTOLOGY("Ontology"),
  ONTOLOGY_ID("OntologyID"),
  IRI("IRI"),

  ENTITY("Entity"),
  CLASS_EXPRESSION_ENTITY("ClassExpression", ENTITY),
  DATA_RANGE_ENTITY("DataRange", ENTITY),
  OBJECT_PROPERTY_EXPRESSION_ENTITY("ObjectPropertyExpression", ENTITY),
  DATA_PROPERTY_EXPRESSION_ENTITY("DataPropertyExpression", ENTITY),
  INDIVIDUAL_ENTITY("Individual", ENTITY),

  CLASS("Class", CLASS_EXPRESSION_ENTITY),
  DATATYPE("Datatype", DATA_RANGE_ENTITY),
  OBJECT_PROPERTY("ObjectProperty", OBJECT_PROPERTY_EXPRESSION_ENTITY),
  DATA_PROPERTY("DataProperty", DATA_PROPERTY_EXPRESSION_ENTITY),
  ANNOTATION_PROPERTY("AnnotationProperty", ENTITY),
  NAMED_INDIVIDUAL("NamedIndividual", INDIVIDUAL_ENTITY),
  INDIVIDUAL("Individual"),
  ANONYMOUS_INDIVIDUAL("AnonymousIndividual", INDIVIDUAL),

  CLASS_EXPRESSION("ClassExpression"),
  OBJECT_INTERSECTION_OF("ObjectIntersectionOf", CLASS_EXPRESSION),
  OBJECT_UNION_OF("ObjectUnionOf", CLASS_EXPRESSION),
  OBJECT_COMPLEMENT_OF("ObjectComplementOf", CLASS_EXPRESSION),
  OBJECT_SOME_VALUES_FROM("ObjectSomeValuesFrom", CLASS_EXPRESSION),
  OBJECT_ALL_VALUES_FROM("ObjectAllValuesFrom", CLASS_EXPRESSION),
  OBJECT_MIN_CARDINALITY("ObjectMinCardinality", CLASS_EXPRESSION),
  OBJECT_EXACT_CARDINALITY("ObjectExactCardinality", CLASS_EXPRESSION),
  OBJECT_MAX_CARDINALITY("ObjectMaxCardinality", CLASS_EXPRESSION),
  DATA_SOME_VALUES_FROM("DataSomeValuesFrom", CLASS_EXPRESSION),
  DATA_ALL_VALUES_FROM("DataAllValuesFrom", CLASS_EXPRESSION),
  DATA_MIN_CARDINALITY("DataMinCardinality", CLASS_EXPRESSION),
  DATA_EXACT_CARDINALITY("DataExactCardinality", CLASS_EXPRESSION),
  DATA_MAX_CARDINALITY("DataMaxCardinality", CLASS_EXPRESSION),
  OBJECT_HAS_VALUE("ObjectHasValue", CLASS_EXPRESSION),
  OBJECT_HAS_SELF("ObjectHasSelf", CLASS_EXPRESSION),
  OBJECT_ONE_OF("ObjectOneOf", CLASS_EXPRESSION),
  DATA_HAS_VALUE("DataHasValue", CLASS_EXPRESSION),

  OBJECT_PROPERTY_EXPRESSION("ObjectPropertyExpression"),
  OBJECT_INVERSE_OF("ObjectInverseOf", OBJECT_PROPERTY_EXPRESSION),

  DATA_RANGE("DataRange"),
  DATA_COMPLEMENT_OF("DataComplementOf", DATA_RANGE),
  DATA_INTERSECTION_OF("DataIntersectionOf", DATA_RANGE),
  DATA_UNION_OF("DataUnionOf", DATA_RANGE),
  DATA_ONE_OF("DataOneOf", DATA_RANGE),
  DATATYPE_RESTRICTION("DatatypeRestriction", DATA_RANGE),
  FACET_RESTRICTION("DatatypeRestriction", DATA_RANGE),

  LITERAL("Literal"),
  LANGUAGE_TAG("LanguageTag"),

  AXIOM("Axiom"),
  DECLARATION("Declaration", AXIOM),
  DATATYPE_DEFINITION("DatatypeDefinition", AXIOM),
  HAS_KEY("HasKey", AXIOM),

  CLASS_AXIOM("ClassAxiom", AXIOM),
  SUB_CLASS_OF("SubClassOf", CLASS_AXIOM),
  EQUIVALENT_CLASSES("EquivalentClasses", CLASS_AXIOM),
  DISJOINT_CLASSES("DisjointClasses", CLASS_AXIOM),
  DISJOINT_UNION("DisjointUnion", CLASS_AXIOM),

  OBJECT_PROPERTY_AXIOM("ObjectPropertyAxiom", AXIOM),
  SUB_OBJECT_PROPERTY_OF("SubObjectPropertyOf", OBJECT_PROPERTY_AXIOM),
  EQUIVALENT_OBJECT_PROPERTIES("EquivalentObjectProperties", OBJECT_PROPERTY_AXIOM),
  DISJOINT_OBJECT_PROPERTIES("DisjointObjectProperties", OBJECT_PROPERTY_AXIOM),
  OBJECT_PROPERTY_DOMAIN("ObjectPropertyDomain", OBJECT_PROPERTY_AXIOM),
  OBJECT_PROPERTY_RANGE("ObjectPropertyRange", OBJECT_PROPERTY_AXIOM),
  INVERSE_OBJECT_PROPERTIES("InverseObjectProperties", OBJECT_PROPERTY_AXIOM),
  FUNCTIONAL_OBJECT_PROPERTY("FunctionalObjectProperty", OBJECT_PROPERTY_AXIOM),
  INVERSE_FUNCTIONAL_OBJECT_PROPERTY("InverseFunctionalObjectProperty", OBJECT_PROPERTY_AXIOM),
  REFLEXIVE_OBJECT_PROPERTY("ReflexiveObjectProperty", OBJECT_PROPERTY_AXIOM),
  IRREFLEXIVE_OBJECT_PROPERTY("IrreflexiveObjectProperty", OBJECT_PROPERTY_AXIOM),
  SYMMETRIC_OBJECT_PROPERTY("SymmetricObjectProperty", OBJECT_PROPERTY_AXIOM),
  ASYMMETRIC_OBJECT_PROPERTY("AsymmetricObjectProperty", OBJECT_PROPERTY_AXIOM),
  TRANSITIVE_OBJECT_PROPERTY("TransitiveObjectProperty", OBJECT_PROPERTY_AXIOM),

  DATA_PROPERTY_AXIOM("DataPropertyAxiom", AXIOM),
  SUB_DATA_PROPERTY_OF("SubDataPropertyOf", DATA_PROPERTY_AXIOM),
  EQUIVALENT_DATA_PROPERTIES("EquivalentDataProperties", DATA_PROPERTY_AXIOM),
  DISJOINT_DATA_PROPERTIES("DisjointDataProperties", DATA_PROPERTY_AXIOM),
  DATA_PROPERTY_DOMAIN("DataPropertyDomain", DATA_PROPERTY_AXIOM),
  DATA_PROPERTY_RANGE("DataPropertyRange", DATA_PROPERTY_AXIOM),
  FUNCTIONAL_DATA_PROPERTY("FunctionalDataProperty", DATA_PROPERTY_AXIOM),

  ASSERTION("Assertion", AXIOM),
  SAME_INDIVIDUAL("SameIndividual", ASSERTION),
  DIFFERENT_INDIVIDUALS("DifferentIndividuals", ASSERTION),
  CLASS_ASSERTION("ClassAssertion", ASSERTION),
  OBJECT_PROPERTY_ASSERTION("ObjectPropertyAssertion", ASSERTION),
  NEGATIVE_OBJECT_PROPERTY_ASSERTION("NegativeObjectPropertyAssertion", ASSERTION),
  DATA_PROPERTY_ASSERTION("DataPropertyAssertion", ASSERTION),
  NEGATIVE_DATA_PROPERTY_ASSERTION("NegativeDataPropertyAssertion", ASSERTION),

  ANNOTATION_AXIOM("AnnotationAxiom", AXIOM),
  ANNOTATION_ASSERTION("AnnotationAssertion", ANNOTATION_AXIOM),
  SUB_ANNOTATION_PROPERTY_OF("SubAnnotationPropertyOf", ANNOTATION_AXIOM),
  ANNOTATION_PROPERTY_DOMAIN("AnnotationPropertyDomain", ANNOTATION_AXIOM),
  ANNOTATION_PROPERTY_RANGE("AnnotationPropertyRange", ANNOTATION_AXIOM),

  ANNOTATION("Annotation"),
  SWRL_RULE("SWRLRule");
  // @formatter:on

  @Nonnull
  private final String label;

  @Nonnull
  private final Optional<NodeLabels> parentLabels;

  @Nonnull
  private final String printLabel;

  @Nonnull
  private final ImmutableList<String> labelList;

  NodeLabels(@Nonnull String label) {
    this(label, Optional.empty());
  }

  NodeLabels(@Nonnull String label,
             @Nonnull NodeLabels parentLabels) {
    this(label, Optional.of(parentLabels));
  }

  NodeLabels(@Nonnull String label,
             @Nonnull Optional<NodeLabels> parentLabels) {
    this.label = checkNotNull(label);
    this.parentLabels = checkNotNull(parentLabels);
    this.labelList = getLabelList();
    this.printLabel = getPrintLabel();
  }

  @Nonnull
  private ImmutableList<String> getLabelList() {
    return labels().collect(ImmutableList.toImmutableList());
  }

  @Nonnull
  public Stream<String> labels() {
    var s1 = Stream.of(label);
    var s2 = getParentLabels().flatMap(NodeLabels::labels);
    return Stream.concat(s1, s2);
  }

  @Nonnull
  private String getPrintLabel() {
    var printLabel = ":" + label;
    if (parentLabels.isPresent()) {
      printLabel += parentLabels.get().printLabels();
    }
    return printLabel;
  }

  private Stream<NodeLabels> getParentLabels() {
    return parentLabels.stream().flatMap(Stream::of);
  }

  public ImmutableList<String> asList() {
    return labelList;
  }

  public boolean isa(NodeLabels nodeLabels) {
    return matchLabel(nodeLabels) || matchParentLabel(nodeLabels);
  }

  private boolean matchLabel(NodeLabels nodeLabels) {
    return label.equals(nodeLabels.label);
  }

  private boolean matchParentLabel(NodeLabels nodeLabels) {
    return (parentLabels.isPresent()) && parentLabels.get().isa(nodeLabels);
  }

  @Nonnull
  public String printLabels() {
    return printLabel;
  }
}