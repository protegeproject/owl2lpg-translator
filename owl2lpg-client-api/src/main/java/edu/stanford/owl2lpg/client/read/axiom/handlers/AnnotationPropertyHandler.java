package edu.stanford.owl2lpg.client.read.axiom.handlers;

import edu.stanford.owl2lpg.client.read.axiom.NodeIndex;
import edu.stanford.owl2lpg.client.read.axiom.NodeMapper;
import edu.stanford.owl2lpg.translator.vocab.PropertyFields;
import org.neo4j.driver.types.Node;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.ANNOTATION_PROPERTY;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class AnnotationPropertyHandler implements NodeHandler<OWLAnnotationProperty> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Inject
  public AnnotationPropertyHandler(@Nonnull OWLDataFactory dataFactory) {
    this.dataFactory = checkNotNull(dataFactory);
  }

  @Override
  public String getLabel() {
    return ANNOTATION_PROPERTY.getMainLabel();
  }

  @Override
  public OWLAnnotationProperty handle(Node node, NodeIndex nodeIndex, NodeMapper nodeMapper) {
    var iri = IRI.create(node.get(PropertyFields.IRI).asString());
    return dataFactory.getOWLAnnotationProperty(iri);
  }
}
