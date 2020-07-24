package edu.stanford.owl2lpg.client.read.hierarchy;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.DatabaseModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = DatabaseModule.class)
public abstract class ClassHierarchyAccessorModule {

  @Binds
  public abstract ClassHierarchyAccessor
  provideClassHierarchyAccessor(ClassHierarchyAccessorImpl impl);
}
