package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.AnnotationPropertyFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationPropertyFrameAccessorFactory
    implements FrameAccessorFactory<AnnotationPropertyFrame> {

  @Override
  public AnnotationPropertyFrameAccessor getAccessor() {
    return new AnnotationPropertyFrameAccessor();
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return AnnotationPropertyFrame.class.equals(frameClass);
  }
}
