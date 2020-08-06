package edu.stanford.owl2lpg.translator.vocab;

import com.fasterxml.jackson.annotation.JsonValue;
import edu.stanford.owl2lpg.model.EdgeType;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.EdgeType.AUGMENTING;
import static edu.stanford.owl2lpg.model.EdgeType.STRUCTURAL;

/**
 * A collection of edge labels used to name the relationship found
 * between the OWL 2 objects.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public enum EdgeLabel {

  ONTOLOGY_IRI(STRUCTURAL),
  VERSION_IRI(STRUCTURAL),
  ONTOLOGY_DOCUMENT(STRUCTURAL),
  BRANCH(STRUCTURAL),
  ONTOLOGY_ANNOTATION(STRUCTURAL),
  AXIOM_ANNOTATION(STRUCTURAL),
  ANNOTATION_ANNOTATION(STRUCTURAL),
  AXIOM(STRUCTURAL),
  ENTITY(STRUCTURAL),
  ENTITY_IRI(STRUCTURAL),
  OBJECT_PROPERTY(STRUCTURAL),
  INDIVIDUAL(STRUCTURAL),
  LITERAL(STRUCTURAL),
  CLASS(STRUCTURAL),
  CLASS_EXPRESSION(STRUCTURAL),
  SUB_CLASS_EXPRESSION(STRUCTURAL),
  SUPER_CLASS_EXPRESSION(STRUCTURAL),
  DISJOINT_CLASS_EXPRESSION(STRUCTURAL),
  OBJECT_PROPERTY_EXPRESSION(STRUCTURAL),
  INVERSE_OBJECT_PROPERTY_EXPRESSION(STRUCTURAL),
  SUB_OBJECT_PROPERTY_EXPRESSION(STRUCTURAL),
  SUPER_OBJECT_PROPERTY_EXPRESSION(STRUCTURAL),
  DATA_PROPERTY_EXPRESSION(STRUCTURAL),
  SUB_DATA_PROPERTY_EXPRESSION(STRUCTURAL),
  SUPER_DATA_PROPERTY_EXPRESSION(STRUCTURAL),
  ANNOTATION_PROPERTY(STRUCTURAL),
  SUB_ANNOTATION_PROPERTY(STRUCTURAL),
  SUPER_ANNOTATION_PROPERTY(STRUCTURAL),
  ANNOTATION_SUBJECT(STRUCTURAL),
  ANNOTATION_VALUE(STRUCTURAL),
  DATA_RANGE(STRUCTURAL),
  DATATYPE(STRUCTURAL),
  RESTRICTION(STRUCTURAL),
  CONSTRAINING_FACET(STRUCTURAL),
  RESTRICTION_VALUE(STRUCTURAL),
  DOMAIN(STRUCTURAL),
  RANGE(STRUCTURAL),
  SOURCE_INDIVIDUAL(STRUCTURAL),
  TARGET_INDIVIDUAL(STRUCTURAL),
  TARGET_VALUE(STRUCTURAL),
  NEXT(STRUCTURAL),
  HAS_DOMAIN(AUGMENTING),
  HAS_RANGE(AUGMENTING),
  AXIOM_OF(AUGMENTING),
  ENTITY_SIGNATURE_OF(AUGMENTING),
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

  @Nonnull
  public boolean isa(EdgeLabel label) {
    return this.equals(label);
  }

  /**
   * The plain name without the prefixed colon
   */
  @JsonValue
  public String getName() {
    return name();
  }

  /**
   * The Neo4J name prefixed with a colon
   */
  @Nonnull
  public String getNeo4jName() {
    return printLabel;
  }
}