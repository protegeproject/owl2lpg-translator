package edu.stanford.owl2lpg.client.read.entity;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.read.entity.impl.EntityAccessorImpl;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class EntityAccessorModule {

  @Binds
  public abstract EntityAccessor provideEntityAccessor(EntityAccessorImpl impl);
}
