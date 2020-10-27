package edu.stanford.owl2lpg.client.bind.project.index;

import com.google.common.collect.ImmutableList;
import org.neo4j.driver.Driver;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.client.util.Resources.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DefaultIndexLoader implements IndexLoader {

  private static final String DEFAULT_INDEX_QUERY_FILE = "index/create-indexes-and-constraints.cpy";

  private static final String DEFAULT_INDEX_QUERY = read(DEFAULT_INDEX_QUERY_FILE);

  @Nonnull
  private final Driver driver;

  @Inject
  public DefaultIndexLoader(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Override
  public boolean createIndexes() {
    try (var session = driver.session()) {
      return session.readTransaction(tx -> {
        try {
          var indexQueries = ImmutableList.copyOf(DEFAULT_INDEX_QUERY.split(";"));
          indexQueries.stream().forEach(tx::run);
          return true;
        } catch (Exception e) {
          return false;
        }
      });
    }
  }
}
