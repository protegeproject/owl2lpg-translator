package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriter;
import edu.stanford.owl2lpg.exporter.csv.writer.bulk.AnyEdgesCsvSchema;
import edu.stanford.owl2lpg.exporter.csv.writer.bulk.AnyNodesCsvSchema;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class NoOpCsvWriterModule {

  @Provides
  @IntoSet
  public CsvWriter<Node> provideNodeCsvWriter() {
    return new NoOpCsvWriter<Node>(
        new CsvMapper(),
        new AnyNodesCsvSchema(),
        new NoOpWriter());
  }

  @Provides
  @IntoSet
  public CsvWriter<Edge> provideEdgeCsvWriter() {
    return new NoOpCsvWriter<Edge>(
        new CsvMapper(),
        new AnyEdgesCsvSchema(),
        new NoOpWriter());
  }
}
