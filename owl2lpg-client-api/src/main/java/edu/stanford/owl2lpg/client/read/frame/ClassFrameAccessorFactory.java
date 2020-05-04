package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameAccessorFactory implements FrameAccessorFactory<ClassFrame> {

  @Override
  public ClassFrameAccessor getAccessor() {
    return new ClassFrameAccessor();
  }

  @Override
  public boolean isAccessorFor(@Nonnull Class<?> frameClass) {
    return ClassFrame.class.equals(frameClass);
  }
}
