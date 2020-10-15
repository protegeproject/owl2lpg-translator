package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlEquivalentObjectPropertiesNodeHandler implements NodeHandler<OWLEquivalentObjectPropertiesAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlEquivalentObjectPropertiesNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                                  @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.EQUIVALENT_OBJECT_PROPERTIES.getMainLabel();
  }

  @Override
  public OWLEquivalentObjectPropertiesAxiom handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var properties = nodeToOwlMapper.toObjectPropertyExprs(mainNode, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLEquivalentObjectPropertiesAxiom(properties, annotations);
  }
}
