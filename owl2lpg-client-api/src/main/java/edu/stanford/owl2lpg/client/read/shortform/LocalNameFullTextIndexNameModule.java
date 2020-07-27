package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;

import javax.annotation.Nonnull;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class LocalNameFullTextIndexNameModule {

  private static final String DEFAULT_INDEX_NAME = "local_name_index";

  @Nonnull
  private final String localNameFullTextIndexName;

  public LocalNameFullTextIndexNameModule(@Nonnull String localNameFullTextIndexName) {
    this.localNameFullTextIndexName = checkNotNull(localNameFullTextIndexName);
  }

  public LocalNameFullTextIndexNameModule() {
    this(DEFAULT_INDEX_NAME);
  }

  @Provides
  @Named("localNameFullTextIndexName")
  @DatabaseSessionScope
  public Neo4jFullTextIndexName getLocalNameFullTextIndexName() {
    return Neo4jFullTextIndexName.create(localNameFullTextIndexName);
  }
}
