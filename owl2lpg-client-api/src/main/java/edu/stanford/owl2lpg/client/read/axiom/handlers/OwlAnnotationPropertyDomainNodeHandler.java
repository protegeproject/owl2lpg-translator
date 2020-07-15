package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlAnnotationPropertyDomainNodeHandler implements NodeHandler<OWLAnnotationPropertyDomainAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlAnnotationPropertyDomainNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                                @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.ANNOTATION_PROPERTY_DOMAIN.getMainLabel();
  }

  @Override
  public OWLAnnotationPropertyDomainAxiom handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var property = nodeToOwlMapper.toAnnotationProperty(mainNode, nodeIndex, nodeMapper);
    var domain = nodeToOwlMapper.toAnnotationPropertyDomain(mainNode, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLAnnotationPropertyDomainAxiom(property, domain, annotations);
  }
}
