package edu.stanford.owl2lpg.translator;

import edu.stanford.owl2lpg.translator.visitors.EntityInProjectVisitor;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class EntityInProjectTranslator {

  @Nonnull
  private final Provider<EntityInProjectVisitor> visitor;

  @Inject
  public EntityInProjectTranslator(@Nonnull Provider<EntityInProjectVisitor> visitor) {
    this.visitor = checkNotNull(visitor);
  }

  @Nonnull
  public Translation translate(OWLEntity entity) {
    checkNotNull(entity);
    return entity.accept(visitor.get());
  }
}
