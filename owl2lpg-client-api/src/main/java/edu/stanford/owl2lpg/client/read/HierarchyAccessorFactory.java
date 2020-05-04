package edu.stanford.owl2lpg.client.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface HierarchyAccessorFactory<T> {

  HierarchyAccessor<T> getAccessor();

  boolean isAccessorFor(Class<?> hierarchyClass);
}
