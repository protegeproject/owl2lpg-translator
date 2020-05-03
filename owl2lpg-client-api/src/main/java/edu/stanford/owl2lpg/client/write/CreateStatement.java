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

  public static CreateStatement create(@Nonnull String cypherQuery,
                                       @Nonnull Session session) {
    return new AutoValue_CreateStatement(cypherQuery, session);
  }

  public boolean run() {
    getSession().writeTransaction(tx -> tx.run(getCypherQuery()));
    return true;
  }

  protected abstract String getCypherQuery();

  protected abstract Session getSession();
}
