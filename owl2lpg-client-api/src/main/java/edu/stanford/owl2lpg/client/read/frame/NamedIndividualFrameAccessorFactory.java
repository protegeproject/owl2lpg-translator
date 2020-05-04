package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.NamedIndividualFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NamedIndividualFrameAccessorFactory
    implements FrameAccessorFactory<NamedIndividualFrame> {

  @Override
  public NamedIndividualFrameAccessor getAccessor() {
    return new NamedIndividualFrameAccessor();
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return NamedIndividualFrame.class.equals(frameClass);
  }
}
