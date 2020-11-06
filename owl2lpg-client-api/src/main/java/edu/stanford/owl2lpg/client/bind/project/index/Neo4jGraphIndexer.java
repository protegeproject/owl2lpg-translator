package edu.stanford.owl2lpg.client.bind.project.index;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class Neo4jGraphIndexer implements GraphIndexer {

  @Nonnull
  private final Set<IndexLoader> indexLoaders;

  @Inject
  public Neo4jGraphIndexer(@Nonnull Set<IndexLoader> indexLoaders) {
    this.indexLoaders = checkNotNull(indexLoaders);
  }

  @Override
  public boolean run() {
    return indexLoaders.stream()
        .map(IndexLoader::createIndexes)
        .reduce(Boolean.TRUE, Boolean::logicalAnd);
  }
}
