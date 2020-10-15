package edu.stanford.owl2lpg.client.bind.change;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.change.OntologyChange;
import edu.stanford.bmir.protege.web.server.index.impl.UpdatableIndex;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jUpdater implements UpdatableIndex {

  @Nonnull
  private final Neo4jChangeVisitor changeVisitor;

  @Inject
  public Neo4jUpdater(@Nonnull Neo4jChangeVisitor changeVisitor) {
    this.changeVisitor = checkNotNull(changeVisitor);
  }

  @Override
  public void applyChanges(@Nonnull ImmutableList<OntologyChange> immutableList) {
    immutableList.forEach(change -> change.accept(changeVisitor));
  }
}
