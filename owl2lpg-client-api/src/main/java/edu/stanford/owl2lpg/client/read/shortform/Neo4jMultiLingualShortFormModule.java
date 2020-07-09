package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;
import edu.stanford.owl2lpg.client.read.axiom.handlers.OwlDataFactoryModule;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = OwlDataFactoryModule.class)
public abstract class Neo4jMultiLingualShortFormModule {

  @Binds
  public abstract MultiLingualShortFormIndex
  provideMultiLingualShortFromIndex(Neo4jMultiLingualShortFormIndex impl);

  @Binds
  public abstract MultiLingualShortFormDictionary
  provideMultiLingualShortFormDictionary(Neo4jMultiLingualShortFormDictionary impl);

  @Binds
  public abstract SearchableMultiLingualShortFormDictionary
  provideSearchableMultiLingualShortFormDictionary(Neo4jSearchableMultiLingualShortFormDictionary impl);
}
