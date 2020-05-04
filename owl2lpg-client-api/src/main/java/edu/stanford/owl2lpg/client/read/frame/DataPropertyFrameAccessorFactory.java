package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.DataPropertyFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataPropertyFrameAccessorFactory
    implements FrameAccessorFactory<DataPropertyFrame> {

  @Override
  public DataPropertyFrameAccessor getAccessor() {
    return new DataPropertyFrameAccessor();
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return DataPropertyFrame.class.equals(frameClass);
  }
}
