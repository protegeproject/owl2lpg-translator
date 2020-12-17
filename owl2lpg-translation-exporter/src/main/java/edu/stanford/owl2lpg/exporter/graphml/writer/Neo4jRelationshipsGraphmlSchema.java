package edu.stanford.owl2lpg.exporter.graphml.writer;

import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlSchema;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static edu.stanford.owl2lpg.model.Edge.*;
import static edu.stanford.owl2lpg.translator.vocab.PropertyFields.*;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jRelationshipsGraphmlSchema implements Neo4jGraphmlSchema {

  @Inject
  public Neo4jRelationshipsGraphmlSchema() {
  }

  private static GraphmlSchema.Builder getBuilder() {
    return GraphmlSchema.builder()
        .addProperty(N4J_JSON_LABELS)
        .addProperty(N4J_JSON_START_ID)
        .addProperty(N4J_JSON_END_ID)
        .addProperty(TYPE)
        .addProperty(IRI)
        .addProperty(STRUCTURAL_SPEC + ":boolean")
        .addProperty(POS + ":int");
  }

  @Override
  @Nonnull
  public GraphmlSchema getGraphmlSchema() {
    return getBuilder().build();
  }

}
