package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlAnnotationNodeHandler implements NodeHandler<OWLAnnotation> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlAnnotationNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                  @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return ANNOTATION.getMainLabel();
  }

  @Override
  public OWLAnnotation handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var property = nodeToOwlMapper.toAnnotationProperty(mainNode, nodeIndex, nodeMapper);
    var value = nodeToOwlMapper.toAnnotationValue(mainNode, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAnnotationAnnotations(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLAnnotation(property, value, annotations);
  }
}
