package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_EXACT_CARDINALITY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDataExactCardinalityNodeHandler implements NodeHandler<OWLDataExactCardinality> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlDataExactCardinalityNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                            @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return DATA_EXACT_CARDINALITY.getMainLabel();
  }

  @Override
  public OWLDataExactCardinality handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var cardinality = mainNode.get(PropertyFields.CARDINALITY).asInt();
    var property = nodeToOwlMapper.toDataPropertyExpr(mainNode, nodeIndex, nodeMapper);
    var filler = nodeToOwlMapper.toDataRange(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLDataExactCardinality(cardinality, property, filler);
  }
}
