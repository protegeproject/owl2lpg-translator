package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.DataPropertyFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import edu.stanford.owl2lpg.client.read.statement.GraphResult;
import edu.stanford.owl2lpg.client.shared.Arguments;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class DataPropertyFrameAccessor extends FrameAccessor<DataPropertyFrame> {

  @Override
  protected String getCypherQuery(Arguments arguments) {
    return null;
  }

  @Override
  protected DataPropertyFrame getFrame(GraphResult result) {
    return null;
  }
}
