package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlObjectPropertyRangeNodeHandler implements NodeHandler<OWLObjectPropertyRangeAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlObjectPropertyRangeNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                           @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.OBJECT_PROPERTY_RANGE.getMainLabel();
  }

  @Override
  public OWLObjectPropertyRangeAxiom handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var property = nodeToOwlMapper.toObjectPropertyExpr(node, nodeIndex, nodeMapper);
    var range = nodeToOwlMapper.toObjectPropertyRange(node, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(node, nodeIndex, nodeMapper);
    return dataFactory.getOWLObjectPropertyRangeAxiom(property, range, annotations);
  }
}
