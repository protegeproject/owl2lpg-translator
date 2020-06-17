package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

@Module
public class CsvExporterModule {

  @Provides
  @TranslationSessionScope
  CsvMapper provideCsvMapper() {
    return new CsvMapper();
  }

  @Provides
  @TranslationSessionScope
  public ExportTracker<Node> provideNodeTracker() {
    return new CacheBasedNodeTracker();
  }

  @Provides
  @TranslationSessionScope
  public ExportTracker<Edge> provideEdgeTracker() {
    return new CacheBasedEdgeTracker();
  }
}
