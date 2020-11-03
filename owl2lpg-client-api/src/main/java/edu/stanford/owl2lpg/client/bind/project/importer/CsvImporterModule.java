package edu.stanford.owl2lpg.client.bind.project.importer;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class CsvImporterModule {

  @Binds
  public abstract CsvImporter provideApocCsvImporter(ApocCsvImporter impl);
}
