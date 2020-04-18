package edu.stanford.owl2lpg.translator.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.Properties;

import javax.annotation.Nonnull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A helper code for building node and edge properties.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertiesBuilder {

  private final Map<String, Object> map;

  private PropertiesBuilder() {
    this(Maps.newHashMap());
  }

  private PropertiesBuilder(@Nonnull Map<String, Object> map) {
    this.map = checkNotNull(Maps.newHashMap(map));
  }

  public static PropertiesBuilder create() {
    return new PropertiesBuilder();
  }

  public static PropertiesBuilder from(@Nonnull Map<String, Object> map) {
    return new PropertiesBuilder(map);
  }

  public PropertiesBuilder set(String key, Object value) {
    if (value != null) {
      map.put(key, value);
    }
    return this;
  }

  public Properties build() {
    return Properties.create(ImmutableMap.copyOf(map));
  }
}
