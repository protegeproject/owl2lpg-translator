package edu.stanford.owl2lpg.translator.internal;

import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.model.NodeIdProvider;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NumberIncrementIdProvider implements NodeIdProvider {

  private long counter = 0;

  @Inject
  public NumberIncrementIdProvider() {
  }

  @Override
  public NodeId getId(Object o) {
    counter++;
    return NodeId.create(counter);
  }
}
