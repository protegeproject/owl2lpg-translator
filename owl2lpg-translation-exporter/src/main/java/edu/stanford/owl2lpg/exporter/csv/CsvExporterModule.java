package edu.stanford.owl2lpg.exporter.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import dagger.Module;
import dagger.Provides;
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
  public CsvExportChecker provideExportChecker() {
    return new CacheBasedCsvExportChecker();
  }
}
