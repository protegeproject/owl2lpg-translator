package edu.stanford.owl2lpg.client;

import edu.stanford.owl2lpg.client.read.DataAccessor;
import edu.stanford.owl2lpg.client.read.DataAccessorFactory;
import edu.stanford.owl2lpg.client.read.MatchStatement;
import edu.stanford.owl2lpg.client.write.AxiomStorer;
import edu.stanford.owl2lpg.client.write.AxiomToCypherQuery;
import edu.stanford.owl2lpg.client.write.CreateStatement;
import edu.stanford.owl2lpg.translator.TranslatorFactory;
import edu.stanford.owl2lpg.versioning.translator.AxiomContextTranslator;
import org.neo4j.driver.*;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Database implements AutoCloseable {

  @Nonnull
  private final Driver driver;

  @Nonnull
  private final DataAccessorFactory dataAccessorFactory;

  public Database(@Nonnull Driver driver,
                  @Nonnull DataAccessorFactory dataAccessorFactory) {
    this.driver = checkNotNull(driver);
    this.dataAccessorFactory = dataAccessorFactory;
  }

  public static Builder builder() {
    return new Builder();
  }

  private Session getSession() {
    return driver.session();
  }

  public AxiomStorer getAxiomStorer() {
    return new AxiomStorer(this,
        getConnection(),
        new AxiomToCypherQuery(
            TranslatorFactory.getAxiomTranslator(),
            new AxiomContextTranslator()));
  }

  public DataAccessor getDataAccessor() {
    return new DataAccessor(this,
        getConnection(),
        dataAccessorFactory);
  }

  public DatabaseConnection getConnection() {
    return new DatabaseConnection(getSession());
  }

  public boolean run(CreateStatement statement) {
    return statement.run();
  }

  public Result run(MatchStatement statement) {
    return statement.run();
  }

  @Override
  public void close() throws Exception {
    driver.close();
  }

  public static class Builder {

    private String uri;
    private String username;
    private String password;
    private DataAccessorFactory dataAccessorFactory;

    public Builder setConnection(@Nonnull String uri,
                                 @Nonnull String username,
                                 @Nonnull String password) {
      this.uri = checkNotNull(uri);
      this.username = checkNotNull(username);
      this.password = checkNotNull(password);
      return this;
    }

    public Builder setAccessors(@Nonnull DataAccessorFactory dataAccessorFactory) {
      this.dataAccessorFactory = checkNotNull(dataAccessorFactory);
      return this;
    }

    public Database connect() {
      var driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
      return new Database(driver, dataAccessorFactory);
    }
  }
}
