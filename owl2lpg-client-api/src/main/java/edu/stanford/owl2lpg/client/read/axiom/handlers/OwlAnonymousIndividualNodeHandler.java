package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANONYMOUS_INDIVIDUAL;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlAnonymousIndividualNodeHandler implements NodeHandler<OWLAnonymousIndividual> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public OwlAnonymousIndividualNodeHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return ANONYMOUS_INDIVIDUAL.getMainLabel();
  }

  @Override
  public OWLAnonymousIndividual handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var nodeId = node.get(PropertyFields.NODE_ID).asString();
    return dataFactory.getOWLAnonymousIndividual(nodeId);
  }
}
