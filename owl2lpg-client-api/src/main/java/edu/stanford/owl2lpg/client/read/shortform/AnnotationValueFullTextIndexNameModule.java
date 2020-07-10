package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Module;
import dagger.Provides;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public class AnnotationValueFullTextIndexNameModule {

  private static final String DEFAULT_INDEX_NAME = "annotation_value_index";

  @Nonnull
  private final String annotationValueFullIndexName;

  public AnnotationValueFullTextIndexNameModule(@Nonnull String annotationValueFullIndexName) {
    this.annotationValueFullIndexName = checkNotNull(annotationValueFullIndexName);
  }

  public AnnotationValueFullTextIndexNameModule() {
    this(DEFAULT_INDEX_NAME);
  }

  @Provides
  @DatabaseSessionScope
  public Neo4jFullTextIndexName getAnnotationValueFullTextIndexName() {
    return Neo4jFullTextIndexName.create(annotationValueFullIndexName);
  }
}
