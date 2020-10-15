package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATA_PROPERTY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDataPropertyNodeHandler implements NodeHandler<OWLDataProperty> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public OwlDataPropertyNodeHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return DATA_PROPERTY.getMainLabel();
  }

  @Override
  public OWLDataProperty handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var iri = IRI.create(node.get(PropertyFields.IRI).asString());
    return dataFactory.getOWLDataProperty(iri);
  }
}
