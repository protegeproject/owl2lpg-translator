package edu.stanford.owl2lpg.translator.vocab;

import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Nonnull;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A collection of edge labels used to name the relationship found
 * between the OWL 2 objects.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum EdgeLabel {

  ONTOLOGY_ID("ontologyID"),
  ONTOLOGY_DOCUMENT("ontologyDocument"),
  BRANCH("branch"),
  ONTOLOGY_ANNOTATION("ontologyAnnotation"),
  AXIOM_ANNOTATION("axiomAnnotation"),
  ANNOTATION_ANNOTATION("annotationAnnotation"),
  AXIOM("axiom"),
  ENTITY("entity"),
  ENTITY_IRI("entityIri"),
  OBJECT_PROPERTY("objectProperty"),
  INDIVIDUAL("individual"),
  LITERAL("literal"),
  CLASS("class"),
  CLASS_EXPRESSION("classExpression"),
  SUB_CLASS_EXPRESSION("subClassExpression"),
  SUPER_CLASS_EXPRESSION("superClassExpression"),
  DISJOINT_CLASS_EXPRESSION("disjointClassExpression"),
  OBJECT_PROPERTY_EXPRESSION("objectPropertyExpression"),
  SUB_OBJECT_PROPERTY_EXPRESSION("subObjectPropertyExpression"),
  SUPER_OBJECT_PROPERTY_EXPRESSION("superObjectPropertyExpression"),
  DATA_PROPERTY_EXPRESSION("dataPropertyExpression"),
  SUB_DATA_PROPERTY_EXPRESSION("subDataPropertyExpression"),
  SUPER_DATA_PROPERTY_EXPRESSION("superDataPropertyExpression"),
  ANNOTATION_PROPERTY("annotationProperty"),
  SUB_ANNOTATION_PROPERTY("subAnnotationProperty"),
  SUPER_ANNOTATION_PROPERTY("superAnnotationProperty"),
  ANNOTATION_SUBJECT("annotationSubject"),
  ANNOTATION_VALUE("annotationValue"),
  DATA_RANGE("dataRange"),
  DATATYPE("datatype"),
  RESTRICTION("restriction"),
  CONSTRAINING_FACET("constrainingFacet"),
  RESTRICTION_VALUE("restrictionValue"),
  LANGUAGE_TAG("languageTag"),
  SOURCE_INDIVIDUAL("sourceIndividual"),
  TARGET_INDIVIDUAL("targetIndividual"),
  TARGET_VALUE("targetValue"),
  DOMAIN("domain"),
  RANGE("range"),
  NEXT("next"),
  SUB_CLASS_OF("subClassOf"),
  SUB_OBJECT_PROPERTY_OF("subObjectPropertyOf"),
  SUB_DATA_PROPERTY_OF("subDataPropertyOf"),
  SUB_ANNOTATION_PROPERTY_OF("subAnnotationPropertyOf"),
  AXIOM_SUBJECT("axiomSubject"),
  RELATED_TO("relatedTo"),
  TYPE("type"),
  SAME_INDIVIDUAL("sameIndividual"),
  INVERSE_OF("inverseOf");

  @Nonnull
  private final String value;

  @Nonnull
  private final String printLabel;

  private final String neo4jName;

  EdgeLabel(@Nonnull String value) {
    this.value = checkNotNull(value);
    neo4jName = LOWER_CAMEL.to(UPPER_UNDERSCORE, value);
    this.printLabel = ":" + neo4jName;
  }

  @Nonnull
  public String getValue() {
    return value;
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