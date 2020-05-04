package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.stanford.owl2lpg.client.DatabaseSession;
import org.neo4j.driver.Result;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class FrameAccessor<T> {

  private final List<Object> parameters = Lists.newArrayList();

  public FrameAccessor<T> setParameter(@Nonnull Object parameter) {
    parameters.add(parameter);
    return this;
  }

  public T getFrame(@Nonnull DatabaseSession session) {
    var query = getCypherQuery(ImmutableList.copyOf(parameters));
    var statement = session.matchStatement(query);
    var result = statement.run();
    return getFrame(result);
  }

  protected abstract String getCypherQuery(ImmutableList<Object> parameters);

  protected abstract T getFrame(Result result);
}
