package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.model.Translation;
import edu.stanford.owl2lpg.translator.visitors.ClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLClassExpression;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 class expressions.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class ClassExpressionTranslator {

  @Nonnull
  private final Provider<ClassExpressionVisitor> visitor;

  @Inject
  public ClassExpressionTranslator(Provider<ClassExpressionVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLClassExpression ce) {
    checkNotNull(ce);
    return ce.accept(visitor.get());
  }
}
