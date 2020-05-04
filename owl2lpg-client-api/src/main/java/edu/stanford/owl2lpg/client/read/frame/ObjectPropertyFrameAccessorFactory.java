package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyFrameAccessorFactory
    implements FrameAccessorFactory<ObjectPropertyFrame> {

  @Override
  public ObjectPropertyFrameAccessor getAccessor() {
    return new ObjectPropertyFrameAccessor();
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return ObjectPropertyFrame.class.equals(frameClass);
  }
}
