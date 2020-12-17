package edu.stanford.owl2lpg.exporter.graphml.writer;

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


  private static GraphmlSchema.Builder getBuilder() {
    return GraphmlSchema.builder()
        .addProperty(N4J_JSON_ID)
        .addProperty(N4J_JSON_LABELS)
        .addProperty(PROJECT_ID)
        .addProperty(BRANCH_ID)
        .addProperty(ONTOLOGY_DOCUMENT_ID)
        .addProperty(IRI)
        .addProperty(LOCAL_NAME)
        .addProperty(PREFIXED_NAME)
        .addProperty(OBO_ID)
        .addProperty(LEXICAL_FORM)
        .addProperty(DATATYPE)
        .addProperty(LANGUAGE)
        .addProperty(NODE_ID)
        .addProperty(CARDINALITY + ":int")
        .addProperty(DIGEST);
  }
}
