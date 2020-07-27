package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlSymmetricObjectPropertyNodeHandler implements NodeHandler<OWLSymmetricObjectPropertyAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlSymmetricObjectPropertyNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                               @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.SYMMETRIC_OBJECT_PROPERTY.getMainLabel();
  }

  @Override
  public OWLSymmetricObjectPropertyAxiom handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var property = nodeToOwlMapper.toObjectPropertyExpr(node, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(node, nodeIndex, nodeMapper);
    return dataFactory.getOWLSymmetricObjectPropertyAxiom(property, annotations);
  }
}
