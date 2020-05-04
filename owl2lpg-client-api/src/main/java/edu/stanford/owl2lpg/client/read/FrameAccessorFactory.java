package edu.stanford.owl2lpg.client.read;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public interface FrameAccessorFactory<T> {

  FrameAccessor<T> getAccessor();

  boolean isAccessorFor(Class<?> frameClass);
}
