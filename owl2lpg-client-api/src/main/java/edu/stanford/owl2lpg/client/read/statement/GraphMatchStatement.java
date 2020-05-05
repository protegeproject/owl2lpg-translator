package edu.stanford.owl2lpg.client.read.statement;

import com.google.auto.value.AutoValue;
import org.neo4j.driver.Session;
import org.neo4j.driver.internal.value.PathValue;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class GraphMatchStatement {

  public static GraphMatchStatement create(@Nonnull String cypherQuery,
                                           @Nonnull Session session) {
    return new AutoValue_GraphMatchStatement(cypherQuery, session);
  }

  public GraphResult run() {
    return getSession().readTransaction(tx -> {
      var builder = new GraphResult.Builder();
      tx.run(getCypherQuery()).stream()
          .forEach(record -> { // row
            record.values().stream() // column
                .filter(PathValue.class::isInstance)
                .map(value -> value.asPath())
                .forEach(path -> path.forEach(builder::add));
          });
      return builder.build();
    });
  }

  public abstract String getCypherQuery();

  protected abstract Session getSession();
}
