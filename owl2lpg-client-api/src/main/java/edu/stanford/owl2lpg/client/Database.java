package edu.stanford.owl2lpg.client;

import edu.stanford.owl2lpg.client.write.AxiomStorer;
import edu.stanford.owl2lpg.client.write.AxiomToCypherQuery;
import edu.stanford.owl2lpg.client.write.CreateQueryStatement;
import edu.stanford.owl2lpg.translator.TranslatorFactory;
import edu.stanford.owl2lpg.versioning.translator.AxiomContextTranslator;
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

  public Database(@Nonnull Driver driver) {
    this.driver = checkNotNull(driver);
  }

  public static Database connect(@Nonnull String uri,
                                 @Nonnull String username,
                                 @Nonnull String password) throws Exception {
    var driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    return new Database(driver);
  }

  public AxiomStorer getAxiomStorer() {
    try (var session = driver.session()) {
      return new AxiomStorer(this,
          session,
          new AxiomToCypherQuery(
              TranslatorFactory.getAxiomTranslator(),
              new AxiomContextTranslator()));
    }
  }

  public boolean run(CreateQueryStatement statement) {
    return statement.run();
  }

  @Override
  public void close() throws Exception {
    driver.close();
  }
}
