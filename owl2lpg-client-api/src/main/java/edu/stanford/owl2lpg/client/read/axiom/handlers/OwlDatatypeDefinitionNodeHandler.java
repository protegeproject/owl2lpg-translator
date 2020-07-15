package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class OwlDatatypeDefinitionNodeHandler implements NodeHandler<OWLDatatypeDefinitionAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeToOwlMapper nodeToOwlMapper;

  @Inject
  public OwlDatatypeDefinitionNodeHandler(@Nonnull OWLDataFactory dataFactory,
                                          @Nonnull NodeToOwlMapper nodeToOwlMapper) {
    this.dataFactory = checkNotNull(dataFactory);
    this.nodeToOwlMapper = checkNotNull(nodeToOwlMapper);
  }

  @Override
  public String getLabel() {
    return NodeLabels.DATATYPE_DEFINITION.getMainLabel();
  }

  @Override
  public OWLDatatypeDefinitionAxiom handle(Node mainNode, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var datatype = nodeToOwlMapper.toDatatype(mainNode, nodeIndex, nodeMapper);
    var dataRange = nodeToOwlMapper.toDataRange(mainNode, nodeIndex, nodeMapper);
    var annotations = nodeToOwlMapper.toAxiomAnnotations(mainNode, nodeIndex, nodeMapper);
    return dataFactory.getOWLDatatypeDefinitionAxiom(datatype, dataRange, annotations);
  }
}
