package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.NamedIndividualFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NamedIndividualFrameAccessorFactory
    implements FrameAccessorFactory<NamedIndividualFrame> {

  @Override
  public NamedIndividualFrameAccessor getAccessor(@Nonnull Database database,
                                                  @Nonnull Session session) {
    return new NamedIndividualFrameAccessor(database, session);
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return NamedIndividualFrame.class.equals(frameClass);
  }
}
