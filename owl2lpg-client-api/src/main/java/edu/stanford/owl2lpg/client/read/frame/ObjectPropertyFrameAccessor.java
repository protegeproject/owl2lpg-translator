package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import org.neo4j.driver.Result;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyFrameAccessor extends FrameAccessor<ObjectPropertyFrame> {

  public ObjectPropertyFrameAccessor(@Nonnull Database database,
                                     @Nonnull DatabaseConnection connection) {
    super(database, connection);
  }

  @Override
  protected String getCypherQuery(ImmutableList<Object> parameters) {
    return null;
  }

  @Override
  protected ObjectPropertyFrame getFrame(Result result) {
    return null;
  }
}
