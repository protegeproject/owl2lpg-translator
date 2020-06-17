package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Provides;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NoOpCsvExporterModule {

  @Provides
  @TranslationSessionScope
  CsvMapper provideCsvMapper() {
    return new CsvMapper();
  }

  @Provides
  @TranslationSessionScope
  public ExportTracker<Node> provideNodeTracker() {
    return new NoOpNodeTracker();
  }

  @Provides
  @TranslationSessionScope
  public ExportTracker<Edge> provideEdgeTracker() {
    return new NoOpEdgeTracker();
  }
}
