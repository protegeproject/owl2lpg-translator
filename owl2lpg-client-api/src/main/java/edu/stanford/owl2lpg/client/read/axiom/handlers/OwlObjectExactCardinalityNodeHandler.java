package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_EXACT_CARDINALITY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlObjectExactCardinalityNodeHandler implements NodeHandler<OWLObjectExactCardinality> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlObjectExactCardinalityNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                              @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return OBJECT_EXACT_CARDINALITY.getMainLabel();
  }

  @Override
  public OWLObjectExactCardinality handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var cardinality = mainNode.get(PropertyFields.CARDINALITY).asInt();
    var property = nodeToOwlMapper.toObjectPropertyExpr(mainNode, nodeIndex, nodeMapper);
    var filler = nodeToOwlMapper.toClassExpr(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLObjectExactCardinality(cardinality, property, filler);
  }
}
