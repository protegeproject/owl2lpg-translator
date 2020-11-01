package edu.stanford.owl2lpg.exporter.csv.writer.bulk;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriter;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class BulkCsvWriterModule {

  @Nonnull
  private final Path importDirectory;

  public BulkCsvWriterModule(@Nonnull Path importDirectory) {
    this.importDirectory = checkNotNull(importDirectory);
  }

  @Provides
  @IntoSet
  public CsvWriter<Node> provideNodeCsvWriter() {
    try {
      var outputFile = importDirectory.resolve("nodes.csv").toFile();
      return new CsvWriter<Node>(
          new CsvMapper(),
          new AnyNodesCsvSchema(),
          new BufferedWriter(new FileWriter(outputFile)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Provides
  @IntoSet
  public CsvWriter<Edge> provideEdgeCsvWriter() {
    try {
      var outputFile = importDirectory.resolve("edges.csv").toFile();
      return new CsvWriter<Edge>(
          new CsvMapper(),
          new AnyEdgesCsvSchema(),
          new BufferedWriter(new FileWriter(outputFile)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
