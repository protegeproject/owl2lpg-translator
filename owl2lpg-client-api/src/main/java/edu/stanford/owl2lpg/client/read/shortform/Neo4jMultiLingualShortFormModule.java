package edu.stanford.owl2lpg.client.read.shortform;

import dagger.Binds;
import dagger.Module;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualDictionary;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormDictionary;
import edu.stanford.bmir.protege.web.server.shortform.MultiLingualShortFormIndex;
import edu.stanford.bmir.protege.web.server.shortform.SearchableMultiLingualShortFormDictionary;

/**
 * @author Josef Hardi <josef.hardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
@Module(includes = {
    Neo4jResultMapperModule.class,
    Neo4jSearchableMultiLingualShortFormModule.class})
public abstract class Neo4jMultiLingualShortFormModule {

  @Binds
  public abstract MultiLingualDictionary
  provideMultiLingualDictionary(Neo4jMultiLingualDictionary impl);

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
