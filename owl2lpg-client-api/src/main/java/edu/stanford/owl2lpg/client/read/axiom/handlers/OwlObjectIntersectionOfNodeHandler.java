package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlObjectIntersectionOfNodeHandler implements NodeHandler<OWLObjectIntersectionOf> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlObjectIntersectionOfNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                            @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.OBJECT_INTERSECTION_OF.getMainLabel();
  }

  @Override
  public OWLObjectIntersectionOf handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var operands = nodeToOwlMapper.toClassExprs(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLObjectIntersectionOf(operands);
  }
}
