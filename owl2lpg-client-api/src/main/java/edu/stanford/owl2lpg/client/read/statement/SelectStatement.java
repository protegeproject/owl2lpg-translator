package edu.stanford.owl2lpg.client.read.statement;

import com.google.auto.value.AutoValue;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class SelectStatement {

  public static SelectStatement create(@Nonnull String cypherQuery,
                                       @Nonnull Session session) {
    return new AutoValue_SelectStatement(cypherQuery, session);
  }

  public SelectResult run() {
    return getSession().readTransaction(tx -> {
      var builder = new SelectResult.Builder();
      tx.run(getCypherQuery()).stream()
          .map(Record::asMap)
          .forEach(builder::add);
      return builder.build();
    });
  }

  public abstract String getCypherQuery();

  protected abstract Session getSession();
}
