package edu.stanford.owl2lpg.client.read.handlers;

import edu.stanford.owl2lpg.client.read.NodeIndex;
import edu.stanford.owl2lpg.client.read.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.DATATYPE;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDatatypeNodeHandler implements NodeHandler<OWLDatatype> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public OwlDatatypeNodeHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return DATATYPE.getMainLabel();
  }

  @Override
  public OWLDatatype handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var iri = IRI.create(node.get(PropertyFields.IRI).asString());
    return dataFactory.getOWLDatatype(iri);
  }
}
