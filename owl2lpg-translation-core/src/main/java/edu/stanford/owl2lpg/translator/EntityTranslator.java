package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.EntityVisitor;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A translator to translate the OWL 2 entities.
 *
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityTranslator {

  @Nonnull
  private final Provider<EntityVisitor> visitor;

  @Inject
  public EntityTranslator(@Nonnull Provider<EntityVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLEntity entity) {
    checkNotNull(entity);
    return entity.accept(visitor.get());
  }
}
