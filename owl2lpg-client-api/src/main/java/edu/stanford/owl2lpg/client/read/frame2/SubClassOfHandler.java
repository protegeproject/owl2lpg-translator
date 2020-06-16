package edu.stanford.owl2lpg.client.read.frame2;

import edu.stanford.owl2lpg.model.Node;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.translator.vocab.EdgeLabel.*;
import static edu.stanford.owl2lpg.translator.vocab.NodeLabels.SUB_CLASS_OF;

public class SubClassOfHandler implements NodeHandler<OWLSubClassOfAxiom> {

  @Nonnull
  private final OWLDataFactory dataFactory;

  @Nonnull
  private final NodeConverter converter;

  @Inject
  public SubClassOfHandler(@Nonnull OWLDataFactory dataFactory,
                           @Nonnull NodeConverter converter) {
    this.dataFactory = checkNotNull(dataFactory);
    this.converter = checkNotNull(converter);
  }

  @Override
  public String getLabel() {
    return SUB_CLASS_OF.getMainLabel();
  }

  @Override
  public OWLSubClassOfAxiom handle(Node node, NodeIndex nodeIndex) {
    var subClassExpr = converter.toObject(node, SUB_CLASS_EXPRESSION.name(), nodeIndex, OWLClassExpression.class);
    var superClassExpr = converter.toObject(node, SUPER_CLASS_EXPRESSION.name(), nodeIndex, OWLClassExpression.class);
    var axiomAnnotations = converter.toObjects(node, AXIOM_ANNOTATION.name(), nodeIndex, OWLAnnotation.class);
    return dataFactory.getOWLSubClassOfAxiom(subClassExpr, superClassExpr, axiomAnnotations);
  }
}
