package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.OBJECT_ALL_VALUES_FROM;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlObjectAllValuesFromNodeHandler implements NodeHandler<OWLObjectAllValuesFrom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlObjectAllValuesFromNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                           @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return OBJECT_ALL_VALUES_FROM.getMainLabel();
  }

  @Override
  public OWLObjectAllValuesFrom handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var property = nodeToOwlMapper.toObjectPropertyExpr(mainNode, nodeIndex, nodeMapper);
    var filler = nodeToOwlMapper.toClassExpr(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLObjectAllValuesFrom(property, filler);
  }
}
