package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import edu.stanford.owl2lpg.client.write.CypherQuery;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyFrameAccessor extends FrameAccessor<ObjectPropertyFrame> {

  public ObjectPropertyFrameAccessor(@Nonnull Database database,
                                     @Nonnull Session session) {
    super(database, session);
  }

  @Override
  protected CypherQuery getCypherQuery(ImmutableList<Object> parameters) {
    return null;
  }

  @Override
  protected ObjectPropertyFrame getFrame(Result result) {
    return null;
  }
}
