package edu.stanford.owl2lpg.client.write;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.client.DatabaseSession;
import edu.stanford.owl2lpg.translator.Translation;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class Storer<T> {

  private Map<Class<?>, Object> parameters = Maps.newHashMap();

  public Storer<T> addParameter(@Nonnull Object parameter) {
    parameters.put(parameter.getClass(), parameter);
    return this;
  }

  public boolean store(@Nonnull DatabaseSession session) {
    var translation = getTranslation(ImmutableMap.copyOf(parameters));
    var query = getCypherQuery(translation);
    var statement = session.createStatement(query);
    return statement.run();
  }

  protected abstract Translation getTranslation(ImmutableMap<Class<?>, Object> parameters);

  protected abstract String getCypherQuery(Translation translation);
}
