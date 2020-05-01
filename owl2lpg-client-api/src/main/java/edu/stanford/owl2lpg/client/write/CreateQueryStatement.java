package edu.stanford.owl2lpg.client.write;

import com.google.auto.value.AutoValue;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class CreateQueryStatement {

  public static CreateQueryStatement create(@Nonnull Session session,
                                            @Nonnull CypherQuery query) {
    return new AutoValue_CreateQueryStatement(session, query);
  }

  public boolean run() {
    getSession().writeTransaction(getQuery()::run);
    return true;
  }

  protected abstract Session getSession();

  protected abstract CypherQuery getQuery();
}
