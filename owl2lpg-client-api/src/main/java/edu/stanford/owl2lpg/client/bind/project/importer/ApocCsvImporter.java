package edu.stanford.owl2lpg.client.bind.project.importer;

import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ApocCsvImporter implements CsvImporter {

  private static final Logger logger = LoggerFactory.getLogger(ApocCsvImporter.class);

  private static final String DIRECTORY_NAME = "$directoryName";

  private static final String APOC_IMPORT_CSV_FILE_QUERY = "import/apoc-import-csv.cpy";

  private static final String APOC_IMPORT_CSV_QUERY = read(APOC_IMPORT_CSV_FILE_QUERY);

  @Nonnull
  private final Driver driver;

  @Inject
  public ApocCsvImporter(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Override
  public boolean loadOntologyDocument(@Nonnull String directoryName) {
    var queryString = APOC_IMPORT_CSV_QUERY.replace(DIRECTORY_NAME, directoryName);
    try (var session = driver.session()) {
      return session.writeTransaction(tx -> {
        try {
          tx.run(queryString);
          return true;
        } catch (Throwable e) {
          logger.error("Error during APOC import CSV call", e);
          return false;
        }
      });
    }
  }
}
