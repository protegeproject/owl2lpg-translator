package edu.stanford.owl2lpg.client;

import edu.stanford.owl2lpg.client.read.MatchStatement;
import edu.stanford.owl2lpg.client.write.CreateStatement;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DatabaseConnection implements AutoCloseable {

  @Nonnull
  private final Session session;

  public DatabaseConnection(@Nonnull Session session) {
    this.session = checkNotNull(session);
  }

  public CreateStatement createStatement(String cypherQuery) {
    return CreateStatement.create(cypherQuery, session);
  }

  public MatchStatement matchStatement(String cypherQuery) {
    return MatchStatement.create(cypherQuery, session);
  }

  @Override
  public void close() throws Exception {
    session.close();
  }
}
