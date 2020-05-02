package edu.stanford.owl2lpg.client.write;

import com.google.auto.value.AutoValue;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class CreateStatement {

  public static CreateStatement create(@Nonnull CypherQuery query,
                                       @Nonnull Session session) {
    return new AutoValue_CreateStatement(query, session);
  }

  public boolean run() {
    getSession().writeTransaction(getQuery()::run);
    return true;
  }

  protected abstract CypherQuery getQuery();

  protected abstract Session getSession();
}
