package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Binds;
import dagger.Module;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module
public abstract class Neo4jNodeTranslatorModule {

  @Binds
  public abstract Neo4jNodeTranslator
  provideNeo4jNodeTranslator(Neo4jNodeTranslatorImpl nodeTranslator);
}
