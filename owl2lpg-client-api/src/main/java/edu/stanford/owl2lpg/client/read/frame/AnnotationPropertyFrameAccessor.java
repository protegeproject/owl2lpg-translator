package edu.stanford.owl2lpg.client.read.frame;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.frame.AnnotationPropertyFrame;
import edu.stanford.owl2lpg.client.Database;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import javax.annotation.Nonnull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationPropertyFrameAccessor extends FrameAccessor<AnnotationPropertyFrame> {

  public AnnotationPropertyFrameAccessor(@Nonnull Database database,
                                         @Nonnull Session session) {
    super(database, session);
  }

  @Override
  protected String getCypherQuery(ImmutableList<Object> parameters) {
    return null;
  }

  @Override
  protected AnnotationPropertyFrame getFrame(Result result) {
    return null;
  }
}
