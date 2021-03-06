package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.EQUIVALENT_CLASSES;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlEquivalentClassesNodeHandler implements NodeHandler<OWLEquivalentClassesAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlEquivalentClassesNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                         @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return EQUIVALENT_CLASSES.getMainLabel();
  }

  @Override
  public OWLEquivalentClassesAxiom handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var classExpressions = nodeToOwlMapper.toClassExprs(mainNode, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLEquivalentClassesAxiom(classExpressions, annotations);
  }
}
