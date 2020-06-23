package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;

public class OwlSubClassOfNodeHandler implements NodeHandler<OWLSubClassOfAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject

  public OwlSubClassOfNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                  @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return SUB_CLASS_OF.getMainLabel();
  }

  @Override
  public OWLSubClassOfAxiom handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var subClassExpr = nodeToOwlMapper.toSubClassExpr(mainNode, nodeIndex, nodeMapper);
    var superClassExpr = nodeToOwlMapper.toSuperClassExpr(mainNode, nodeIndex, nodeMapper);
    var axiomAnnotations = nodeToOwlMapper.toAxiomAnnotations(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLSubClassOfAxiom(subClassExpr, superClassExpr, axiomAnnotations);
  }
}
