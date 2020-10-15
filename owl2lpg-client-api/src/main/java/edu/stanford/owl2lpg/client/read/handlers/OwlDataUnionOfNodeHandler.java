package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataUnionOf;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDataUnionOfNodeHandler implements NodeHandler<OWLDataUnionOf> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlDataUnionOfNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                   @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.DATA_UNION_OF.getMainLabel();
  }

  @Override
  public OWLDataUnionOf handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var dataRanges = nodeToOwlMapper.toDataRanges(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLDataUnionOf(dataRanges);
  }
}
