package edu.stanford.owl2lpg.exporter.csv.writer;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface CsvSchema<T> {

  @Nonnull
  com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchema();

  @Nonnull
  com.fasterxml.jackson.dataformat.csv.CsvSchema getCsvSchemaWithHeader();

  boolean isCompatible(T object);
}