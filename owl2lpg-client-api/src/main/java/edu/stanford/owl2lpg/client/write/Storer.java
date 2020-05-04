package edu.stanford.owl2lpg.client.write;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.translator.Translation;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class Storer<T> {

  @Nonnull
  private final Database database;

  @Nonnull
  private final DatabaseConnection connection;

  private Map<Class<?>, Object> parameters = Maps.newHashMap();

  protected Storer(@Nonnull Database database,
                   @Nonnull DatabaseConnection connection) {
    this.database = database;
    this.connection = connection;
  }

  public Storer<T> addParameter(Object parameter) {
    parameters.put(parameter.getClass(), parameter);
    return this;
  }

  public boolean store() {
    var translation = getTranslation(ImmutableMap.copyOf(parameters));
    var query = getCypherQuery(translation);
    var statement = connection.createStatement(query);
    return database.run(statement);
  }

  protected abstract Translation getTranslation(ImmutableMap<Class<?>, Object> parameters);

  protected abstract String getCypherQuery(Translation translation);
}
