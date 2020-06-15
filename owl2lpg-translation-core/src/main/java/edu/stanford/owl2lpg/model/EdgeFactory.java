package edu.stanford.owl2lpg.model;

import edu.stanford.owl2lpg.translator.vocab.EdgeLabel;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class EdgeFactory {

  @Inject
  public EdgeFactory() {
  }

  public Edge createEdge(@Nonnull Node fromNode,
                         @Nonnull Node toNode,
                         @Nonnull EdgeLabel edgeLabel,
                         @Nonnull Properties properties) {
    return Edge.create(fromNode, toNode, edgeLabel, properties);
  }

  public Edge createEdge(@Nonnull Node fromNode,
                         @Nonnull Node toNode,
                         @Nonnull EdgeLabel edgeLabel) {
    var properties = edgeLabel.isStructural() ?
        Properties.of(PropertyFields.STRUCTURAL_SPEC, true) :
        Properties.empty();
    return createEdge(fromNode, toNode, edgeLabel, properties);
  }
}
