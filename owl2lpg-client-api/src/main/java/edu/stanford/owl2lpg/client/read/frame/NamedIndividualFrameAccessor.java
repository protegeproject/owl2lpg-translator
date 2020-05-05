package edu.stanford.owl2lpg.client.read.frame;

import edu.stanford.bmir.protege.web.shared.frame.NamedIndividualFrame;
import edu.stanford.owl2lpg.client.read.FrameAccessor;
import edu.stanford.owl2lpg.client.read.statement.GraphResult;
import edu.stanford.owl2lpg.client.shared.Arguments;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NamedIndividualFrameAccessor extends FrameAccessor<NamedIndividualFrame> {

  @Override
  protected String getCypherQuery(Arguments arguments) {
    return null;
  }

  @Override
  protected NamedIndividualFrame getFrame(GraphResult result) {
    return null;
  }
}
