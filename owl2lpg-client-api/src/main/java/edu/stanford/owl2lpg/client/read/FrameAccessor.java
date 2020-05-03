package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import org.neo4j.driver.Result;

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
  private final DatabaseConnection connection;

  private final List<Object> parameters = Lists.newArrayList();

  public FrameAccessor(@Nonnull Database database,
                       @Nonnull DatabaseConnection connection) {
    this.database = checkNotNull(database);
    this.connection = checkNotNull(connection);
  }

  public FrameAccessor<T> setParameter(Object parameter) {
    parameters.add(parameter);
    return this;
  }

  public T getFrame() {
    var query = getCypherQuery(ImmutableList.copyOf(parameters));
    var statement = connection.matchStatement(query);
    var result = database.run(statement);
    return getFrame(result);
  }

  protected abstract String getCypherQuery(ImmutableList<Object> parameters);

  protected abstract T getFrame(Result result);
}
