package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameAccessorFactory implements FrameAccessorFactory<ClassFrame> {

  @Override
  public ClassFrameAccessor getAccessor(@Nonnull Database database,
                                        @Nonnull Session session) {
    return new ClassFrameAccessor(database, session);
  }

  @Override
  public boolean isAccessorFor(@Nonnull Class<?> frameClass) {
    return ClassFrame.class.equals(frameClass);
  }
}
