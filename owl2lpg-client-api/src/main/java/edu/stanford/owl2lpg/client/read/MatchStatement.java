package edu.stanford.owl2lpg.client.read;

import com.google.auto.value.AutoValue;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class MatchStatement {

  public static MatchStatement create(@Nonnull String cypherQuery,
                                      @Nonnull Session session) {
    return new AutoValue_MatchStatement(cypherQuery, session);
  }

  public Result run() {
    return getSession().writeTransaction(tx -> tx.run(getCypherQuery()));
  }

  public abstract String getCypherQuery();

  protected abstract Session getSession();
}
