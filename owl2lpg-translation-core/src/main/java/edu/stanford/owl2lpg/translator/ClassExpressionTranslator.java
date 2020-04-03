package edu.stanford.owl2lpg.translator;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * A translator to translate the OWL 2 class expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionTranslator {

  @Nonnull
  private final OWLClassExpressionVisitorEx<Translation> visitor;

  @Inject
  public ClassExpressionTranslator(OWLClassExpressionVisitorEx<Translation> visitor) {
    this.visitor = visitor;
  }

  @Nonnull
  public Translation visit(OWLClassExpression ce) {
    return ce.accept(visitor);
  }
}
