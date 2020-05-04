package edu.stanford.owl2lpg.client.write;

import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.client.DatabaseSession;
import edu.stanford.owl2lpg.client.shared.Arguments;
import edu.stanford.owl2lpg.translator.Translation;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class Storer<T> {

  private Map<String, Object> arguments = Maps.newHashMap();

  public Storer<T> addArgument(@Nonnull String parameter,
                               @Nonnull Object argument) {
    arguments.put(parameter, argument);
    return this;
  }

  public boolean store(@Nonnull DatabaseSession session) {
    var translation = getTranslation(Arguments.create(arguments));
    var query = getCypherQuery(translation);
    var statement = session.createStatement(query);
    return statement.run();
  }

  protected abstract Translation getTranslation(Arguments arguments);

  protected abstract String getCypherQuery(Translation translation);
}
