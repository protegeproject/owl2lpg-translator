package edu.stanford.owl2lpg.client;

import edu.stanford.owl2lpg.client.write.AxiomStorer;
import edu.stanford.owl2lpg.client.write.CypherStringFactory;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.TranslatorFactory;
import edu.stanford.owl2lpg.versioning.translator.AxiomContextTranslator;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;

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
    return new AxiomStorer(this,
        new AxiomContextTranslator(),
        TranslatorFactory.getAxiomTranslator());
  }

  public void insert(Node node) {
    System.out.println(CypherStringFactory.createNodeQuery(node));
    execute(CypherStringFactory.createNodeQuery(node));
  }

  public void insert(Edge edge) {
    System.out.println(CypherStringFactory.createEdgeQuery(edge));
    execute(CypherStringFactory.createEdgeQuery(edge));
  }

  public Result execute(String cypherString) {
    try (var session = driver.session()) {
      return session.writeTransaction(tx -> tx.run(cypherString));
    }
  }

  @Override
  public void close() throws Exception {
    driver.close();
  }
}
