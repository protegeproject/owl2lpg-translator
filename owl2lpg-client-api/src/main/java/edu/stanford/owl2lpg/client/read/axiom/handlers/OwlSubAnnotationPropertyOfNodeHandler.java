package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlSubAnnotationPropertyOfNodeHandler implements NodeHandler<OWLSubAnnotationPropertyOfAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlSubAnnotationPropertyOfNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                               @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.SUB_ANNOTATION_PROPERTY_OF.getMainLabel();
  }

  @Override
  public OWLSubAnnotationPropertyOfAxiom handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var subProperty = nodeToOwlMapper.toSubAnnotationProperty(node, nodeIndex, nodeMapper);
    var superProperty = nodeToOwlMapper.toSuperAnnotationProperty(node, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(node, nodeIndex, nodeMapper);
    return dataFactory.getOWLSubAnnotationPropertyOfAxiom(subProperty, superProperty, annotations);
  }
}
