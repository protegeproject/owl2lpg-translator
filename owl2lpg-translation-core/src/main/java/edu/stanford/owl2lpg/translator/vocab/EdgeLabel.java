package edu.stanford.owl2lpg.translator.vocab;

import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.owl2lpg.model.EdgeType;

import javax.annotation.Nonnull;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.EdgeType.AUGMENTING;
import static edu.stanford.owl2lpg.model.EdgeType.STRUCTURAL_SPEC;

/**
 * A collection of edge labels used to name the relationship found
 * between the OWL 2 objects.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum EdgeLabel {

  ONTOLOGY_ID("ontologyID", STRUCTURAL_SPEC),
  ONTOLOGY_DOCUMENT("ontologyDocument", STRUCTURAL_SPEC),
  BRANCH("branch", STRUCTURAL_SPEC),
  ONTOLOGY_ANNOTATION("ontologyAnnotation", STRUCTURAL_SPEC),
  AXIOM_ANNOTATION("axiomAnnotation", STRUCTURAL_SPEC),
  ANNOTATION_ANNOTATION("annotationAnnotation", STRUCTURAL_SPEC),
  AXIOM("axiom", STRUCTURAL_SPEC),
  ENTITY("entity", STRUCTURAL_SPEC),
  ENTITY_IRI("entityIri", STRUCTURAL_SPEC),
  OBJECT_PROPERTY("objectProperty", STRUCTURAL_SPEC),
  INDIVIDUAL("individual", STRUCTURAL_SPEC),
  LITERAL("literal", STRUCTURAL_SPEC),
  CLASS("class", STRUCTURAL_SPEC),
  CLASS_EXPRESSION("classExpression", STRUCTURAL_SPEC),
  SUB_CLASS_EXPRESSION("subClassExpression", STRUCTURAL_SPEC),
  SUPER_CLASS_EXPRESSION("superClassExpression", STRUCTURAL_SPEC),
  DISJOINT_CLASS_EXPRESSION("disjointClassExpression", STRUCTURAL_SPEC),
  OBJECT_PROPERTY_EXPRESSION("objectPropertyExpression", STRUCTURAL_SPEC),
  SUB_OBJECT_PROPERTY_EXPRESSION("subObjectPropertyExpression", STRUCTURAL_SPEC),
  SUPER_OBJECT_PROPERTY_EXPRESSION("superObjectPropertyExpression", STRUCTURAL_SPEC),
  DATA_PROPERTY_EXPRESSION("dataPropertyExpression", STRUCTURAL_SPEC),
  SUB_DATA_PROPERTY_EXPRESSION("subDataPropertyExpression", STRUCTURAL_SPEC),
  SUPER_DATA_PROPERTY_EXPRESSION("superDataPropertyExpression", STRUCTURAL_SPEC),
  ANNOTATION_PROPERTY("annotationProperty", STRUCTURAL_SPEC),
  SUB_ANNOTATION_PROPERTY("subAnnotationProperty", STRUCTURAL_SPEC),
  SUPER_ANNOTATION_PROPERTY("superAnnotationProperty", STRUCTURAL_SPEC),
  ANNOTATION_SUBJECT("annotationSubject", STRUCTURAL_SPEC),
  ANNOTATION_VALUE("annotationValue", STRUCTURAL_SPEC),
  DATA_RANGE("dataRange", STRUCTURAL_SPEC),
  DATATYPE("datatype", STRUCTURAL_SPEC),
  RESTRICTION("restriction", STRUCTURAL_SPEC),
  CONSTRAINING_FACET("constrainingFacet", STRUCTURAL_SPEC),
  RESTRICTION_VALUE("restrictionValue", STRUCTURAL_SPEC),
  LANGUAGE_TAG("languageTag", STRUCTURAL_SPEC),
  SOURCE_INDIVIDUAL("sourceIndividual", STRUCTURAL_SPEC),
  TARGET_INDIVIDUAL("targetIndividual", STRUCTURAL_SPEC),
  TARGET_VALUE("targetValue", STRUCTURAL_SPEC),
  DOMAIN("domain", AUGMENTING),
  RANGE("range", AUGMENTING),
  NEXT("next", STRUCTURAL_SPEC),
  SUB_CLASS_OF("subClassOf", AUGMENTING),
  SUB_OBJECT_PROPERTY_OF("subObjectPropertyOf", AUGMENTING),
  SUB_DATA_PROPERTY_OF("subDataPropertyOf", AUGMENTING),
  SUB_ANNOTATION_PROPERTY_OF("subAnnotationPropertyOf", AUGMENTING),
  AXIOM_SUBJECT("axiomSubject", AUGMENTING),
  RELATED_TO("relatedTo", AUGMENTING),
  TYPE("type", AUGMENTING),
  SAME_INDIVIDUAL("sameIndividual", AUGMENTING),
  INVERSE_OF("inverseOf", AUGMENTING);

  @Nonnull
  private final String value;

  @Nonnull
  private final EdgeType edgeType;

  @Nonnull
  private final String printLabel;

  private final String neo4jName;

  EdgeLabel(@Nonnull String value, @Nonnull EdgeType edgeType) {
    this.value = checkNotNull(value);
    this.edgeType = checkNotNull(edgeType);
    neo4jName = LOWER_CAMEL.to(UPPER_UNDERSCORE, value);
    this.printLabel = ":" + neo4jName;
  }

  @Nonnull
  public String getValue() {
    return value;
  }

  @Nonnull
  public EdgeType getEdgeType() {
    return edgeType;
  }

  /**
   * Gets the neo4j name for the edge label.  This is the camelCaseEdgeLabel
   * formatted to be UPPER_SNAKE_CASE
   */
  @JsonValue
  public String getNeo4jName() {
    return neo4jName;
  }

  /**
   * The Neo4J name prefixed with a colon
   */
  @Nonnull
  public String printLabel() {
    return printLabel;
  }
}