package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.NAMED_INDIVIDUAL;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlNamedIndividualNodeHandler implements NodeHandler<OWLNamedIndividual> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public OwlNamedIndividualNodeHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return NAMED_INDIVIDUAL.getMainLabel();
  }

  @Override
  public OWLNamedIndividual handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var iri = IRI.create(node.get(PropertyFields.IRI).asString());
    return dataFactory.getOWLNamedIndividual(iri);
  }
}
