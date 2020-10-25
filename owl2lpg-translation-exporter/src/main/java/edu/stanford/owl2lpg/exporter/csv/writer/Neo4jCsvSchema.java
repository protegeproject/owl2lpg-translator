package edu.stanford.owl2lpg.exporter.csv.writer;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface Neo4jCsvSchema {

  @Nonnull
  CsvSchema getCsvSchema();

  @Nonnull
  CsvSchema getCsvSchemaWithHeader();
}
