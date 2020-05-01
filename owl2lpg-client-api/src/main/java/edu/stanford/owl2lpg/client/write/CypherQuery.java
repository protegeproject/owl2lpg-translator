package edu.stanford.owl2lpg.client.write;

import com.google.auto.value.AutoValue;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class CypherQuery {

  public static CypherQuery create(@Nonnull String cypherString) {
    checkNotNull(cypherString);
    return new AutoValue_CypherQuery(cypherString);
  }

  public Result run(@Nonnull Transaction transaction) {
    return transaction.run(getString());
  }

  public abstract String getString();
}
