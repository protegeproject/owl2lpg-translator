package edu.stanford.owl2lpg.exporter.graphml.writer;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Neo4jGraphmlSchema {

  @Nonnull
  CsvSchema getGraphmlSchema();

  @Nonnull
  CsvSchema getGraphmlSchemaWithHeader();
}
