package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLFacetRestriction;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlFacetRestrictionNodeHandler implements NodeHandler<OWLFacetRestriction> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlFacetRestrictionNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                        @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.FACET_RESTRICTION.getMainLabel();
  }

  @Override
  public OWLFacetRestriction handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var facet = nodeToOwlMapper.toConstrainingFacet(node, nodeIndex, nodeMapper);
    var facetValue = nodeToOwlMapper.toRestrictionValue(node, nodeIndex, nodeMapper);
    return dataFactory.getOWLFacetRestriction(facet, facetValue);
  }
}
