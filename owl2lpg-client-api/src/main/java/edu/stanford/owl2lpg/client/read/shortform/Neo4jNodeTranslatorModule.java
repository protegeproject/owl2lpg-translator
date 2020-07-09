package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Binds;
import dagger.Module;
import edu.stanford.owl2lpg.client.DatabaseSessionScope;
import edu.stanford.owl2lpg.client.read.axiom.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = OwlDataFactoryModule.class)
public abstract class Neo4jNodeTranslatorModule {

  @Binds
  @DatabaseSessionScope
  public abstract Neo4jNodeTranslator
  provideNeo4jNodeTranslator(Neo4jNodeTranslatorImpl nodeTranslator);
}
