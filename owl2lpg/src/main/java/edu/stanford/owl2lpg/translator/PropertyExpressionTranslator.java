package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 property expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionTranslator {

  @Nonnull
  private final OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor;

  @Inject
  public PropertyExpressionTranslator(@Nonnull OWLPropertyExpressionVisitorEx<Translation> propertyExpressionVisitor) {
    this.propertyExpressionVisitor = checkNotNull(propertyExpressionVisitor);
  }

  @Nonnull
  public Translation translate(OWLPropertyExpression ope) {
    return ope.accept(propertyExpressionVisitor);
  }
}
