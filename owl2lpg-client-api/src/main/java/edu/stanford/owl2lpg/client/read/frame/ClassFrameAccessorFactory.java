package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameAccessorFactory implements FrameAccessorFactory<ClassFrame> {

  @Override
  public ClassFrameAccessor getAccessor(@Nonnull Database database,
                                        @Nonnull DatabaseConnection connection) {
    return new ClassFrameAccessor(database, connection);
  }

  @Override
  public boolean isAccessorFor(@Nonnull Class<?> frameClass) {
    return ClassFrame.class.equals(frameClass);
  }
}
