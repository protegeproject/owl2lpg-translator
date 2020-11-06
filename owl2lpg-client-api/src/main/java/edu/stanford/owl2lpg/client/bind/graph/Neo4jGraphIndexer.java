package edu.stanford.owl2lpg.client.bind.graph;

import edu.stanford.bmir.protege.web.server.graph.GraphIndexer;
import edu.stanford.bmir.protege.web.server.graph.IndexBuilder;

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
  private final Set<IndexBuilder> indexBuilders;

  @Inject
  public Neo4jGraphIndexer(@Nonnull Set<IndexBuilder> indexBuilders) {
    this.indexBuilders = checkNotNull(indexBuilders);
  }

  @Override
  public boolean run() {
    return indexBuilders.stream()
        .map(IndexBuilder::buildIndex)
        .reduce(Boolean.TRUE, Boolean::logicalAnd);
  }
}
