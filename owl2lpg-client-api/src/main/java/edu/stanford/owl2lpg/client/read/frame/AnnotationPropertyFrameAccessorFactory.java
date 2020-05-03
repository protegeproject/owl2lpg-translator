package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.AnnotationPropertyFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationPropertyFrameAccessorFactory
    implements FrameAccessorFactory<AnnotationPropertyFrame> {

  @Override
  public AnnotationPropertyFrameAccessor getAccessor(@Nonnull Database database,
                                                     @Nonnull DatabaseConnection connection) {
    return new AnnotationPropertyFrameAccessor(database, connection);
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return AnnotationPropertyFrame.class.equals(frameClass);
  }
}
