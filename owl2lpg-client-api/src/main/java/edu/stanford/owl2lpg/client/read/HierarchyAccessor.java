package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class HierarchyAccessor<T> {

  private final Map<String, Object> arguments = Maps.newHashMap();

  public HierarchyAccessor<T> setArgument(@Nonnull String parameter,
                                          @Nonnull Object argument) {
    arguments.put(parameter, argument);
    return this;
  }
}
