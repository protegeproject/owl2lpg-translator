package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Edge;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.*;

/**
 * A visitor that contains the implementation to translate the OWL 2 property expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionVisitor implements OWLPropertyExpressionVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

  private Node mainNode;

  @Inject
  public PropertyExpressionVisitor(@Nonnull OWLEntityVisitorEx<Translation> entityVisitor) {
    this.entityVisitor = checkNotNull(entityVisitor);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLDataProperty dp) {
    return entityVisitor.visit(dp);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    return entityVisitor.visit(ap);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    return entityVisitor.visit(op);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectInverseOf ope) {
    mainNode = createMainNode(ope, NodeLabels.OBJECT_INVERSE_OF);
    var inverseProperty = ope.getInverseProperty();
    var objectPropertyEdge = createEdge(inverseProperty, EdgeLabels.OBJECT_PROPERTY);
    var inversePropertyTranslation = createTranslation(inverseProperty);
    return Translation.create(mainNode,
        ImmutableList.of(objectPropertyEdge),
        ImmutableList.of(inversePropertyTranslation));
  }

  @Nonnull
  protected Node createMainNode(@Nonnull OWLPropertyExpression property,
                                @Nonnull ImmutableList<String> nodeLabels) {
    checkNotNull(property);
    checkNotNull(nodeLabels);
    return Node(nodeLabels, withIdentifierFrom(property));
  }

  @Nonnull
  protected Edge createEdge(@Nonnull OWLPropertyExpression property,
                            @Nonnull String edgeLabel) {
    checkNotNull(property);
    checkNotNull(edgeLabel);
    var toNode = getMainNode(property);
    return Edge(mainNode, toNode, edgeLabel);
  }

  @Nonnull
  private Node getMainNode(@Nonnull OWLPropertyExpression property) {
    return visit(property).getMainNode();
  }

  @Nonnull
  protected Translation createTranslation(@Nonnull OWLPropertyExpression property) {
    return visit(property);
  }

  @Nonnull
  protected Translation visit(@Nonnull OWLPropertyExpression anyPropertyExpression) {
    checkNotNull(anyPropertyExpression);
    return anyPropertyExpression.accept(this);
  }
}
