package edu.stanford.owl2lpg.exporter.csv.writer.apoc;

import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Node.N4J_JSON_ID;
import static edu.stanford.owl2lpg.model.Node.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANONYMOUS_INDIVIDUAL;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.NODE_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnonymousIndividualNodeSchema implements CsvSchema<Node> {

  @Inject
  public AnonymousIndividualNodeSchema() {
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
    return node.isTypeOf(ANONYMOUS_INDIVIDUAL);
  }

  private static com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder getBuilder() {
    return com.fasterxml.jackson.dataformat.csv.CsvSchema.builder()
        .addColumn(N4J_JSON_ID)
        .addColumn(N4J_JSON_LABELS)
        .addColumn(NODE_ID);
  }
}
