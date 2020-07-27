package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;

import javax.inject.Inject;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class IriNodeHandler implements NodeHandler<IRI> {

  @Inject
  public IriNodeHandler() {
  }

  @Override
  public String getLabel() {
    return NodeLabels.IRI.getMainLabel();
  }

  @Override
  public IRI handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    return IRI.create(node.get(PropertyFields.IRI).asString());
  }
}
