package edu.stanford.owl2lpg.client.read;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public abstract class HierarchyAccessor<T> {

  private final List<Object> parameters = Lists.newArrayList();

  public HierarchyAccessor<T> setParameter(@Nonnull Object parameter) {
    parameters.add(parameter);
    return this;
  }
}
