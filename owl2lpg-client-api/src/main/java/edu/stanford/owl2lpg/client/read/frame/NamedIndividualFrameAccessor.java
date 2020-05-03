package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.frame.NamedIndividualFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.DatabaseConnection;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import org.neo4j.driver.Result;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NamedIndividualFrameAccessor extends FrameAccessor<NamedIndividualFrame> {

  public NamedIndividualFrameAccessor(@Nonnull Database database,
                                      @Nonnull DatabaseConnection connection) {
    super(database, connection);
  }

  @Override
  protected String getCypherQuery(ImmutableList<Object> parameters) {
    return null;
  }

  @Override
  protected NamedIndividualFrame getFrame(Result result) {
    return null;
  }
}
