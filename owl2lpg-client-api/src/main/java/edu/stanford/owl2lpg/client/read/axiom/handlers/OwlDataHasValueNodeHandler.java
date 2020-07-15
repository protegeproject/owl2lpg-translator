package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDataHasValueNodeHandler implements NodeHandler<OWLDataHasValue> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlDataHasValueNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                    @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.DATA_HAS_VALUE.getMainLabel();
  }

  @Override
  public OWLDataHasValue handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var property = nodeToOwlMapper.toDataPropertyExpr(mainNode, nodeIndex, nodeMapper);
    var value = nodeToOwlMapper.toLiteral(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLDataHasValue(property, value);
  }
}
