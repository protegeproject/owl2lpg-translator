package edu.stanford.owl2lpg.client.read.hierarchy;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class HierarchyAccessorModule {

  @Binds
  public abstract ClassHierarchyAccessor
  provideClassHierarchyAccessor(ClassHierarchyAccessorImpl impl);

  @Binds
  public abstract ObjectPropertyHierarchyAccessor
  provideObjectPropertyHierarchyAccessor(ObjectPropertyHierarchyAccessorImpl impl);

  @Binds
  public abstract DataPropertyHierarchyAccessor
  provideDataPropertyHierarchyAccessor(DataPropertyHierarchyAccessorImpl impl);

  @Binds
  public abstract AnnotationPropertyHierarchyAccessor
  provideAnnotationPropertyHierarchyAccessor(AnnotationPropertyHierarchyAccessorImpl impl);
}
