package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.NodeId;
import edu.stanford.owl2lpg.translator.visitors.NodeIdProvider;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class NumberIncrementIdProvider implements NodeIdProvider {

  private final AtomicInteger counter = new AtomicInteger(0);

  @Override
  public NodeId getId() {
    var numberSequence = counter.incrementAndGet();
    return NodeId.create(numberSequence);
  }
}
