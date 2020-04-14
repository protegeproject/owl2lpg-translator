package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.model.Node;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A visitor that contains the implementation to translate the OWL 2 property expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionVisitor extends VisitorBase
    implements OWLPropertyExpressionVisitorEx<Translation> {

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
    checkNotNull(dp);
    return entityVisitor.visit(dp);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLAnnotationProperty ap) {
    checkNotNull(ap);
    return entityVisitor.visit(ap);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectProperty op) {
    checkNotNull(op);
    return entityVisitor.visit(op);
  }

  @Nonnull
  @Override
  public Translation visit(@Nonnull OWLObjectInverseOf ope) {
    mainNode = createNode(ope, NodeLabels.OBJECT_INVERSE_OF);
    var inverseProperty = ope.getInverseProperty();
    var objectPropertyEdge = createEdge(inverseProperty, EdgeLabels.OBJECT_PROPERTY);
    var inversePropertyTranslation = createNestedTranslation(inverseProperty);
    return Translation.create(mainNode,
        ImmutableList.of(objectPropertyEdge),
        ImmutableList.of(inversePropertyTranslation));
  }

  @Nonnull
  @Override
  protected Node getMainNode() {
    return mainNode;
  }

  @Nonnull
  @Override
  protected Translation getTranslation(@Nonnull OWLObject anyObject) {
    checkNotNull(anyObject);
    if (anyObject instanceof OWLPropertyExpression) {
      return ((OWLPropertyExpression) anyObject).accept(this);
    }
    throw new IllegalArgumentException("Implementation error");
  }
}
