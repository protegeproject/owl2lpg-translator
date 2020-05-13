package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.ObjectPropertyFrame;
import org.neo4j.driver.Record;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ObjectPropertyFrameRecordHandler {

  @Nonnull
  private final FrameStructureRecordHandler recordHandler;

  @Inject
  public ObjectPropertyFrameRecordHandler(@Nonnull FrameStructureRecordHandler recordHandler) {
    this.recordHandler = checkNotNull(recordHandler);
  }

  public ObjectPropertyFrame translate(Record record) {
    var map = record.asMap();
    return ObjectPropertyFrame.get(
        recordHandler.getObjectProperty(map),
        recordHandler.getAnnotationValues(map),
        recordHandler.getDomains(map),
        recordHandler.getRanges(map),
        recordHandler.getInverseProperties(map),
        recordHandler.getObjectPropertyCharacteristics(map));
  }
}
