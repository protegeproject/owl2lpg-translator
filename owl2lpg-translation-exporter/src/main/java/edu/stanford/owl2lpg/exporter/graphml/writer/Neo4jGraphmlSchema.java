package edu.stanford.owl2lpg.exporter.graphml.writer;

import edu.stanford.owl2lpg.exporter.graphml.wip.GraphmlSchema;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Neo4jGraphmlSchema {

  @Nonnull
  GraphmlSchema getGraphmlSchema();

  @Nonnull
  GraphmlSchema getGraphmlSchemaWithHeader();
}
