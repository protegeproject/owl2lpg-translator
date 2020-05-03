package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.DataPropertyFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.client.read.FrameAccessorFactory;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataPropertyFrameAccessorFactory
    implements FrameAccessorFactory<DataPropertyFrame> {

  @Override
  public DataPropertyFrameAccessor getAccessor(@Nonnull Database database,
                                               @Nonnull DatabaseConnection connection) {
    return new DataPropertyFrameAccessor(database, connection);
  }

  @Override
  public boolean isAccessorFor(Class<?> frameClass) {
    return DataPropertyFrame.class.equals(frameClass);
  }
}
