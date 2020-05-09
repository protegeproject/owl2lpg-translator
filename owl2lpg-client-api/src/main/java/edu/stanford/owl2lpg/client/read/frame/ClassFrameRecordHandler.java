package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ClassFrame;
import org.neo4j.driver.Record;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassFrameRecordHandler {

  @Nonnull
  private final SharedFrameResultHandler sharedResultHandler;

  @Inject
  public ClassFrameRecordHandler(@Nonnull SharedFrameResultHandler sharedResultHandler) {
    this.sharedResultHandler = checkNotNull(sharedResultHandler);
  }

  public ClassFrame translate(Record record) {
    var map = record.asMap();
    return ClassFrame.get(
        sharedResultHandler.getSubject(map),
        sharedResultHandler.getClassEntries(map),
        sharedResultHandler.getPropertyValues(map));
  }
}
