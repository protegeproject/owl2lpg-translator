package edu.stanford.owl2lpg.model;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Deprecated
public interface NodeIdMapper {

  @Nonnull
  NodeId get(@Nonnull Object o);
}
