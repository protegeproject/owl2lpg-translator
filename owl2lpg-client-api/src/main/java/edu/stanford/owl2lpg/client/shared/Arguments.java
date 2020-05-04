package edu.stanford.owl2lpg.client.shared;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Arguments {

  public static Arguments create(Map<String, Object> arguments) {
    return new AutoValue_Arguments(ImmutableMap.copyOf(arguments));
  }

  public <T> T get(String parameter) {
    var obj = getArguments().get(parameter);
    return (T) obj;
  }

  public abstract ImmutableMap<String, Object> getArguments();
}
