package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.PropertyExpressionVisitor;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 property expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class PropertyExpressionTranslator {

  @Nonnull
  private final Provider<PropertyExpressionVisitor> visitor;

  @Inject
  public PropertyExpressionTranslator(@Nonnull Provider<PropertyExpressionVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLPropertyExpression ope) {
    checkNotNull(ope);
    return ope.accept(visitor.get());
  }
}
