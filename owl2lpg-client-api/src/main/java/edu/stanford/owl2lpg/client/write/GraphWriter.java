package edu.stanford.owl2lpg.client.write;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.summary.SummaryCounters;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class GraphWriter {

  @Nonnull
  private final Driver driver;

  @Inject
  public GraphWriter(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  @Nonnull
  public SummaryCounters execute(@Nonnull String queryString, @Nonnull Value inputParams) {
    try (var session = driver.session()) {
      return session.writeTransaction(tx ->
          tx.run(queryString, inputParams).consume().counters());
    }
  }
}
