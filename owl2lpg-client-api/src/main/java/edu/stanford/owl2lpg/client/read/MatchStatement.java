package edu.stanford.owl2lpg.client.read;

import com.google.auto.value.AutoValue;
import edu.stanford.owl2lpg.client.write.CypherQuery;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class MatchStatement {

  public static MatchStatement create(@Nonnull CypherQuery query,
                                      @Nonnull Session session) {
    return new AutoValue_MatchStatement(query, session);
  }

  public Result run() {
    return getSession().writeTransaction(getQuery()::run);
  }

  public abstract CypherQuery getQuery();

  protected abstract Session getSession();
}
