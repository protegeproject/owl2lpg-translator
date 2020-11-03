package edu.stanford.owl2lpg.client.bind.project.importer;

import org.neo4j.driver.Driver;
import org.neo4j.driver.internal.value.MapValue;
import org.neo4j.driver.internal.value.StringValue;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ApocCsvImporter implements CsvImporter {

  private static final String DIRECTORY_NAME = "directoryName";

  private static final String APOC_IMPORT_CSV_FILE_QUERY = "import/apoc-import-csv.cpy";

  private static final String APOC_IMPORT_CSV_QUERY = read(APOC_IMPORT_CSV_FILE_QUERY);

  @Nonnull
  private final Driver driver;

  @Inject
  public ApocCsvImporter(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Override
  public boolean loadOntologyProject(@Nonnull String directoryName) {
    var inputParam = new MapValue(Map.of(DIRECTORY_NAME, new StringValue(directoryName)));
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        try {
          tx.run(APOC_IMPORT_CSV_QUERY, inputParam);
          return true;
        } catch (Exception e) {
          return false;
        }
      });
    }
  }
}
