package edu.stanford.owl2lpg.client;

import edu.stanford.owl2lpg.client.read.AccessorFactory;
import edu.stanford.owl2lpg.client.read.DataAccessor;
import edu.stanford.owl2lpg.client.read.statement.GraphMatchStatement;
import edu.stanford.owl2lpg.client.read.statement.GraphResult;
import edu.stanford.owl2lpg.client.write.CreateStatement;
import edu.stanford.owl2lpg.client.write.DataStorer;
import edu.stanford.owl2lpg.client.write.StorerFactory;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

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
  private final StorerFactory storerFactory;

  @Nonnull
  private final AccessorFactory accessorFactory;

  public Database(@Nonnull Driver driver,
                  @Nonnull StorerFactory storerFactory,
                  @Nonnull AccessorFactory accessorFactory) {
    this.driver = checkNotNull(driver);
    this.storerFactory = checkNotNull(storerFactory);
    this.accessorFactory = checkNotNull(accessorFactory);
  }

  public static Builder builder() {
    return new Builder();
  }

  public DataStorer getDataStorer() {
    return new DataStorer(getSession(), storerFactory);
  }

  public DataAccessor getDataAccessor() {
    return new DataAccessor(getSession(), accessorFactory);
  }

  public DatabaseSession getSession() {
    return new DatabaseSession(driver.session());
  }

  public boolean run(CreateStatement statement) {
    return statement.run();
  }

  public GraphResult run(GraphMatchStatement statement) {
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
    private StorerFactory storerFactory;
    private AccessorFactory accessorFactory;

    public Builder setConnection(@Nonnull String uri,
                                 @Nonnull String username,
                                 @Nonnull String password) {
      this.uri = checkNotNull(uri);
      this.username = checkNotNull(username);
      this.password = checkNotNull(password);
      return this;
    }

    public Builder setStorers(@Nonnull StorerFactory storerFactory) {
      this.storerFactory = checkNotNull(storerFactory);
      return this;
    }

    public Builder setAccessors(@Nonnull AccessorFactory accessorFactory) {
      this.accessorFactory = checkNotNull(accessorFactory);
      return this;
    }

    public Database connect() {
      var driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
      return new Database(driver, storerFactory, accessorFactory);
    }
  }
}
