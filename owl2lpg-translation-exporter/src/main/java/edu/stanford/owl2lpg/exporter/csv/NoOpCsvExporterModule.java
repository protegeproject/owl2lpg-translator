package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.translator.TranslationSessionScope;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class NoOpCsvExporterModule {

  @Provides
  @TranslationSessionScope
  CsvMapper provideCsvMapper() {
    return new CsvMapper();
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
