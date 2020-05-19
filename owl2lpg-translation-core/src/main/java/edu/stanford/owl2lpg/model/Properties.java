package edu.stanford.owl2lpg.model;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@AutoValue
public abstract class Properties {

  private static final Properties EMPTY = create(ImmutableMap.of());

  public static Properties empty() {
    return EMPTY;
  }

  public static Properties create(@Nonnull ImmutableMap<String, Object> map) {
    return new AutoValue_Properties(map);
  }

  public abstract ImmutableMap<String, Object> getMap();

  @Nullable
  public <E> E get(String key) {
    var obj = getMap().get(key);
    return (obj != null) ? (E) obj.getClass().cast(obj) : null;
  }
}
