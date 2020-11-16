package edu.stanford.owl2lpg.exporter.csv.writer.bulk;

import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvSchema;
import edu.stanford.owl2lpg.model.Edge;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_END_ID;
import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.model.Edge.N4J_JSON_START_ID;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.IRI;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.POS;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.TYPE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnyEdgesCsvSchema implements CsvSchema<Edge> {

  @Inject
  public AnyEdgesCsvSchema() {
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
  public boolean isCompatible(Edge edge) {
    return true;
  }

  private static Builder getBuilder() {
    return com.fasterxml.jackson.dataformat.csv.CsvSchema.builder()
        .addColumn(N4J_JSON_LABELS)
        .addColumn(N4J_JSON_START_ID)
        .addColumn(N4J_JSON_END_ID)
        .addColumn(TYPE)
        .addColumn(IRI)
        .addColumn(POS + ":int");
  }
}
