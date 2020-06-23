package edu.stanford.owl2lpg.client.read.axiom;

import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.CLASS;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassHandler implements NodeHandler<OWLClass> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public ClassHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return CLASS.getMainLabel();
  }

  @Override
  public OWLClass handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var iri = IRI.create(node.get(PropertyFields.IRI).asString());
    return dataFactory.getOWLClass(iri);
  }
}
