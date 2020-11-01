package edu.stanford.owl2lpg.exporter.csv.writer.apoc;

import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Node.N4J_JSON_ID;
import static edu.stanford.owl2lpg.model.Node.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_RANGE;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ENTITY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_PROPERTY_EXPRESSION;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_ID;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.PROPERTY_CHAIN;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SWRL_RULE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultNodeSchema implements CsvSchema<Node> {

  @Inject
  public DefaultNodeSchema() {
  }

  @Nonnull
  @Override
  public com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchema() {
    return getBuilder().build();
  }

  @Nonnull
  @Override
  public com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchemaWithHeader() {
    return getBuilder().setUseHeader(true).build();
  }

  @Override
  public boolean isCompatible(Node node) {
    return node.isTypeOf(ONTOLOGY) ||
        node.isTypeOf(ONTOLOGY_ID) ||
        node.isTypeOf(PROPERTY_CHAIN) ||
        (node.isTypeOf(CLASS_EXPRESSION) && !node.isTypeOf(ENTITY)) ||
        (node.isTypeOf(OBJECT_PROPERTY_EXPRESSION) && !node.isTypeOf(ENTITY)) ||
        (node.isTypeOf(DATA_RANGE) && !node.isTypeOf(ENTITY)) ||
        node.isTypeOf(ANNOTATION) ||
        node.isTypeOf(SWRL_RULE);
  }

  private static com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder getBuilder() {
    return com.fasterxml.jackson.dataformat.csv.CsvSchema.builder()
        .addColumn(N4J_JSON_ID)
        .addColumn(N4J_JSON_LABELS);
  }
}
