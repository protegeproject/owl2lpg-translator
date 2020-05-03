package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.write.CypherQuery;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class FrameAccessor<T> {

  @Nonnull
  private final Database database;

  @Nonnull
  private final Session session;

  private final List<Object> parameters = Lists.newArrayList();

  public FrameAccessor(@Nonnull Database database,
                       @Nonnull Session session) {
    this.database = checkNotNull(database);
    this.session = checkNotNull(session);
  }

  public FrameAccessor<T> setParameter(Object parameter) {
    parameters.add(parameter);
    return this;
  }

  public T getFrame() {
    var query = getCypherQuery(ImmutableList.copyOf(parameters));
    var statement = MatchStatement.create(query, session);
    var result = database.run(statement);
    return getFrame(result);
  }

  protected abstract CypherQuery getCypherQuery(ImmutableList<Object> parameters);

  protected abstract T getFrame(Result result);
}
