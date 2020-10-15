package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
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
  private final Provider<OWLPropertyExpressionVisitorEx<Translation>> visitor;

  @Inject
  public PropertyExpressionTranslator(@Nonnull Provider<OWLPropertyExpressionVisitorEx<Translation>> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLPropertyExpression ope) {
    checkNotNull(ope);
    return ope.accept(visitor.get());
  }
}
