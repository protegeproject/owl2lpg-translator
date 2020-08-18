package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Binds;
import dagger.Module;

import javax.inject.Named;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class Neo4jSearchableMultiLingualShortFormModule {

  @Binds
  @Named("fullTextSearchByAnnotationAssertion")
  public abstract Neo4jFullTextSearch
  providesNeo4jFullTextSearchByAnnotationValue(Neo4jFullTextSearchByAnnotationAssertion impl);

  @Binds
  @Named("fullTextSearchByLocalName")
  public abstract Neo4jFullTextSearch
  providesNeo4jFullTextSearchByLocalName(Neo4jFullTextSearchByLocalName impl);
}
