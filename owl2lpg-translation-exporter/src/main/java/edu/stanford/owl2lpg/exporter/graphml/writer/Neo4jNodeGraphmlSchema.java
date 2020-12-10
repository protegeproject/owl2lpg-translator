package edu.stanford.owl2lpg.exporter.graphml.writer;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlSchema;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Node.N4J_JSON_ID;
import static edu.stanford.owl2lpg.model.Node.N4J_JSON_LABELS;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jNodeGraphmlSchema implements Neo4jGraphmlSchema {

  @Inject
  public Neo4jNodeGraphmlSchema() {
  }

  @Override
  @Nonnull
  public GraphmlSchema getGraphmlSchema() {
    return getBuilder().build();
  }

  @Override
  @Nonnull
  public GraphmlSchema getGraphmlSchemaWithHeader() {
    return getBuilder().setUseHeader(true).build();
  }

  private static GraphmlSchema.Builder getBuilder() {
    return GraphmlSchema.builder()
        .addColumn(N4J_JSON_ID)
        .addColumn(N4J_JSON_LABELS)
        .addColumn(PROJECT_ID)
        .addColumn(BRANCH_ID)
        .addColumn(ONTOLOGY_DOCUMENT_ID)
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
