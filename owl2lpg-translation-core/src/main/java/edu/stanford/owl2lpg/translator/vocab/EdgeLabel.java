package edu.stanford.owl2lpg.translator.vocab;

import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.owl2lpg.model.EdgeType;

import javax.annotation.Nonnull;

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

  ONTOLOGY_ID(STRUCTURAL_SPEC),
  ONTOLOGY_DOCUMENT(STRUCTURAL_SPEC),
  BRANCH(STRUCTURAL_SPEC),
  ONTOLOGY_ANNOTATION(STRUCTURAL_SPEC),
  AXIOM_ANNOTATION(STRUCTURAL_SPEC),
  ANNOTATION_ANNOTATION(STRUCTURAL_SPEC),
  AXIOM(STRUCTURAL_SPEC),
  ENTITY(STRUCTURAL_SPEC),
  ENTITY_IRI(STRUCTURAL_SPEC),
  OBJECT_PROPERTY(STRUCTURAL_SPEC),
  INDIVIDUAL(STRUCTURAL_SPEC),
  LITERAL(STRUCTURAL_SPEC),
  CLASS(STRUCTURAL_SPEC),
  CLASS_EXPRESSION(STRUCTURAL_SPEC),
  SUB_CLASS_EXPRESSION(STRUCTURAL_SPEC),
  SUPER_CLASS_EXPRESSION(STRUCTURAL_SPEC),
  DISJOINT_CLASS_EXPRESSION(STRUCTURAL_SPEC),
  OBJECT_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  INVERSE_OBJECT_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  SUB_OBJECT_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  SUPER_OBJECT_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  DATA_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  SUB_DATA_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  SUPER_DATA_PROPERTY_EXPRESSION(STRUCTURAL_SPEC),
  ANNOTATION_PROPERTY(STRUCTURAL_SPEC),
  SUB_ANNOTATION_PROPERTY(STRUCTURAL_SPEC),
  SUPER_ANNOTATION_PROPERTY(STRUCTURAL_SPEC),
  ANNOTATION_SUBJECT(STRUCTURAL_SPEC),
  ANNOTATION_VALUE(STRUCTURAL_SPEC),
  DATA_RANGE(STRUCTURAL_SPEC),
  DATATYPE(STRUCTURAL_SPEC),
  RESTRICTION(STRUCTURAL_SPEC),
  CONSTRAINING_FACET(STRUCTURAL_SPEC),
  RESTRICTION_VALUE(STRUCTURAL_SPEC),
  DOMAIN(STRUCTURAL_SPEC),
  RANGE(STRUCTURAL_SPEC),
  SOURCE_INDIVIDUAL(STRUCTURAL_SPEC),
  TARGET_INDIVIDUAL(STRUCTURAL_SPEC),
  TARGET_VALUE(STRUCTURAL_SPEC),
  NEXT(STRUCTURAL_SPEC),
  HAS_DOMAIN(AUGMENTING),
  HAS_RANGE(AUGMENTING),
  ENTITY_SIGNATURE(AUGMENTING),
  SUB_CLASS_OF(AUGMENTING),
  SUB_OBJECT_PROPERTY_OF(AUGMENTING),
  SUB_DATA_PROPERTY_OF(AUGMENTING),
  SUB_ANNOTATION_PROPERTY_OF(AUGMENTING),
  AXIOM_SUBJECT(AUGMENTING),
  RELATED_TO(AUGMENTING),
  TYPE(AUGMENTING),
  SAME_INDIVIDUAL(AUGMENTING),
  INVERSE_OF(AUGMENTING);

  @Nonnull
  private final EdgeType edgeType;

  @Nonnull
  private final String printLabel;

  EdgeLabel(@Nonnull EdgeType edgeType) {
    this.edgeType = checkNotNull(edgeType);
    this.printLabel = ":" + name();
  }

  @Nonnull
  public EdgeType getEdgeType() {
    return edgeType;
  }

  public boolean isStructural() {
    return edgeType.equals(STRUCTURAL_SPEC);
  }

  /**
   * Gets the neo4j name for the edge label.  This is the camelCaseEdgeLabel
   * formatted to be UPPER_SNAKE_CASE
   */
  @JsonValue
  public String getNeo4jName() {
    return name();
  }

  /**
   * The Neo4J name prefixed with a colon
   */
  @Nonnull
  public String printLabel() {
    return printLabel;
  }
}