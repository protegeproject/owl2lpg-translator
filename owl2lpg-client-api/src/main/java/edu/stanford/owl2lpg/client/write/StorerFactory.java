package edu.stanford.owl2lpg.client.write;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class StorerFactory {

  @Nonnull
  private final ImmutableMap<Class<?>, ImmutableMap<Mode, Storer>> storerMap;

  public StorerFactory(@Nonnull ImmutableMap<Class<?>, ImmutableMap<Mode, Storer>> storerMap) {
    this.storerMap = checkNotNull(storerMap);
  }

  @Nonnull
  public <T> Storer<T> getStorer(@Nonnull Class<T> objectClass,
                                 @Nonnull Mode storeMode) {
    var storerMapByMode = storerMap.get(objectClass);
    checkStorerNotNull(storerMapByMode, objectClass);
    var storer = storerMapByMode.get(storeMode);
    checkStorerNotNull(storer, storeMode);
    return storer;
  }

  private static void checkStorerNotNull(Object storer, Class<?> objectClass) {
    if (storer == null) {
      throw new IllegalArgumentException(format("Unable to get storer for %s", objectClass));
    }
  }

  private static void checkStorerNotNull(Object storer, Mode storeMode) {
    if (storer == null) {
      throw new IllegalArgumentException(format("Unable to get storer for mode %s", storeMode));
    }
  }
}
