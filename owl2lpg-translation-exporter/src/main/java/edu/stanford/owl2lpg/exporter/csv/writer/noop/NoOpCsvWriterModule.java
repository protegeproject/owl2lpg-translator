package edu.stanford.owl2lpg.exporter.csv.writer.noop;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.exporter.csv.writer.CsvWriter;
import edu.stanford.owl2lpg.exporter.csv.writer.EdgeTracker;
import edu.stanford.owl2lpg.exporter.csv.writer.Neo4jNodeCsvSchema;
import edu.stanford.owl2lpg.exporter.csv.writer.Neo4jRelationshipsCsvSchema;
import edu.stanford.owl2lpg.exporter.csv.writer.NodeTracker;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class NoOpCsvWriterModule {

  @Provides
  @TranslationSessionScope
  public CsvWriter<Node> provideNodeCsvWriter() {
    return new NoOpCsvWriter<Node>(
        new CsvMapper(),
        new Neo4jNodeCsvSchema(),
        new NoOpWriter());
  }

  @Provides
  @TranslationSessionScope
  public CsvWriter<Edge> provideEdgeCsvWriter() {
    return new NoOpCsvWriter<Edge>(
        new CsvMapper(),
        new Neo4jRelationshipsCsvSchema(),
        new NoOpWriter());
  }

  @Provides
  @TranslationSessionScope
  public NodeTracker provideNodeTracker() {
    return new NoOpNodeTracker();
  }

  @Provides
  @TranslationSessionScope
  public EdgeTracker provideEdgeTracker() {
    return new NoOpEdgeTracker();
  }
}
