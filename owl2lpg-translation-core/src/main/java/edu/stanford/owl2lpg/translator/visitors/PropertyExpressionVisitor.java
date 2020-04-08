package edu.stanford.owl2lpg.translator.visitors;

import com.google.common.collect.ImmutableList;
import edu.stanford.owl2lpg.translator.Translation;
import edu.stanford.owl2lpg.translator.vocab.EdgeLabels;
import edu.stanford.owl2lpg.translator.vocab.NodeLabels;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.owl2lpg.model.GraphFactory.*;
import static edu.stanford.owl2lpg.translator.Translation.MainNode;

/**
 * A visitor that contains the implementation to translate the OWL 2 property expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionVisitor implements OWLPropertyExpressionVisitorEx<Translation> {

  @Nonnull
  private final OWLEntityVisitorEx<Translation> entityVisitor;

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
    var inverseNode = Node(NodeLabels.OBJECT_INVERSE_OF, withIdentifierFrom(ope));
    var propertyTranslation = ope.getInverseProperty().accept(this);
    return Translation.create(inverseNode,
        ImmutableList.of(
            Edge(inverseNode, MainNode(propertyTranslation), EdgeLabels.OBJECT_PROPERTY)),
        ImmutableList.of(
            propertyTranslation));
  }
}
