package edu.stanford.owl2lpg.exporter.csv.writer.apoc;

import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Node.N4J_JSON_ID;
import static edu.stanford.owl2lpg.model.Node.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ONTOLOGY_DOCUMENT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IS_DEFAULT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OntologyDocumentNodeSchema implements CsvSchema<Node> {

  @Inject
  public OntologyDocumentNodeSchema() {
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
    return node.isTypeOf(ONTOLOGY_DOCUMENT);
  }

  private static com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder getBuilder() {
    return com.fasterxml.jackson.dataformat.csv.CsvSchema.builder()
        .addColumn(N4J_JSON_ID)
        .addColumn(N4J_JSON_LABELS)
        .addColumn(ONTOLOGY_DOCUMENT_ID)
        .addColumn(IS_DEFAULT + ":boolean");
  }
}
