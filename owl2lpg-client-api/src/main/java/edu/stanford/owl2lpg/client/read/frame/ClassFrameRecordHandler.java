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
  private final FrameStructureRecordHandler recordHandler;

  @Inject
  public ClassFrameRecordHandler(@Nonnull FrameStructureRecordHandler recordHandler) {
    this.recordHandler = checkNotNull(recordHandler);
  }

  public ClassFrame translate(Record record) {
    var map = record.asMap();
    return ClassFrame.get(
        recordHandler.getClassSubject(map),
        recordHandler.getClassEntries(map),
        recordHandler.getPropertyValues(map));
  }
}
