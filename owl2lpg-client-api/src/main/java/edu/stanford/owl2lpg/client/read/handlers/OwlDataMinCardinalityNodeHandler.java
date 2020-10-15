package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_MIN_CARDINALITY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDataMinCardinalityNodeHandler implements NodeHandler<OWLDataMinCardinality> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlDataMinCardinalityNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                          @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return DATA_MIN_CARDINALITY.getMainLabel();
  }

  @Override
  public OWLDataMinCardinality handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var cardinality = mainNode.get(PropertyFields.CARDINALITY).asInt();
    var property = nodeToOwlMapper.toDataPropertyExpr(mainNode, nodeIndex, nodeMapper);
    var filler = nodeToOwlMapper.toDataRange(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLDataMinCardinality(cardinality, property, filler);
  }
}
