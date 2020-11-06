package edu.stanford.owl2lpg.exporter.csv.writer.bulk;

import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Node.N4J_JSON_ID;
import static edu.stanford.owl2lpg.model.Node.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.BRANCH_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.CARDINALITY;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.DATATYPE;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.DIGEST;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IS_DEFAULT;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.LANGUAGE;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.LEXICAL_FORM;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.LOCAL_NAME;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.NODE_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.OBO_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.ONTOLOGY_DOCUMENT_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PREFIXED_NAME;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.PROJECT_ID;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnyNodesCsvSchema implements CsvSchema<Node> {

  @Inject
  public AnyNodesCsvSchema() {
  }

  @Override
  @Nonnull
  public com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchema() {
    return getBuilder().build();
  }

  @Override
  @Nonnull
  public com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchemaWithHeader() {
    return getBuilder().setUseHeader(true).build();
  }

  @Override
  public boolean isCompatible(Node node) {
    return true;
  }

  private static com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder getBuilder() {
    return com.fasterxml.jackson.dataformat.csv.CsvSchema.builder()
        .addColumn(N4J_JSON_ID)
        .addColumn(N4J_JSON_LABELS)
        .addColumn(PROJECT_ID)
        .addColumn(BRANCH_ID)
        .addColumn(ONTOLOGY_DOCUMENT_ID)
        .addColumn(IS_DEFAULT + ":boolean")
        .addColumn(IRI)
        .addColumn(LOCAL_NAME)
        .addColumn(PREFIXED_NAME)
        .addColumn(OBO_ID)
        .addColumn(LEXICAL_FORM)
        .addColumn(DATATYPE)
        .addColumn(LANGUAGE)
        .addColumn(NODE_ID)
        .addColumn(CARDINALITY + ":int")
        .addColumn(DIGEST);
  }
}
