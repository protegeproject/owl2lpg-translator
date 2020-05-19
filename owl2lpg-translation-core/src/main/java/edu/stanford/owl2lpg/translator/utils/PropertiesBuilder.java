package edu.stanford.owl2lpg.translator.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import edu.stanford.owl2lpg.model.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A helper code for building node and edge properties.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertiesBuilder {

  @Nullable
  private final Map<String, Object> map;

  private PropertiesBuilder(@Nonnull Map<String, Object> map) {
    this.map = checkNotNull(Maps.newHashMap(map));
  }

  public static PropertiesBuilder create() {
    return new PropertiesBuilder(new HashMap<>(2));
  }

  public PropertiesBuilder set(String key, Object value) {
    if (value != null) {
      map.put(key, value);
    }
    return this;
  }

  public Properties build() {
    if(map.isEmpty()) {
      return Properties.empty();
    }
    else {
      return Properties.create(ImmutableMap.copyOf(map));
    }
  }
}
