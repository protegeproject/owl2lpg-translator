package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.client.DatabaseSession;
import edu.stanford.owl2lpg.client.shared.Arguments;
import org.neo4j.driver.Result;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class FrameAccessor<T> {

  private final Map<String, Object> arguments = Maps.newHashMap();

  public FrameAccessor<T> setArgument(@Nonnull String parameter,
                                      @Nonnull Object argument) {
    arguments.put(parameter, argument);
    return this;
  }

  public T getFrame(@Nonnull DatabaseSession session) {
    var query = getCypherQuery(Arguments.create(arguments));
    var statement = session.matchStatement(query);
    var result = statement.run();
    return getFrame(result);
  }

  protected abstract String getCypherQuery(Arguments arguments);

  protected abstract T getFrame(Result result);
}
